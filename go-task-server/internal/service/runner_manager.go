package service

import (
	"bytes"
	"encoding/json"
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"net/http"
	"strings"
	"time"
)

var client = &http.Client{
	Timeout: 5 * time.Second,
}

// TaskNodeSelect 任务节点选择算法接口
type TaskNodeSelect interface {
	doSelect(mode int, nodeId string) *common.NodeItem
}

func NewTaskNodeSelect() TaskNodeSelect {
	return &roundTaskNodeSelect{0}
}

// RoundTaskNodeSelect 简易轮询算法实现类
type roundTaskNodeSelect struct {
	taskNodeIndex int
}

func (r *roundTaskNodeSelect) doSelect(mode int, nodeId string) *common.NodeItem {
	if mode == 1 {
		return common.GetTaskNode(nodeId)
	}
	taskNodes := common.ListTaskNodeByStatus(0)
	r.taskNodeIndex++
	if r.taskNodeIndex >= len(taskNodes) {
		r.taskNodeIndex = 0
	}
	return taskNodes[r.taskNodeIndex]
}

// 添加任务到任务运行通道需要的缓存参数
type taskRunner struct {
	*models.Task        // 任务详情
	mode         int    // 任务的运行模式 0：轮询 1：指定
	nodeId       string // 当mode == 1 时，指定运行的任务节点
}

// AllotsTaskParams 用于下发给客户端的执行任务参数
type AllotsTaskParams struct {
	TaskId      int        `json:"taskId"`      // 需要运行的任务ID
	RecordId    int        `json:"recordId"`    // 任务的运行记录ID
	TaskType    int        `json:"taskType"`    // 任务类型
	HandlerType int        `json:"handlerType"` // 任务的处理器类型
	HandlerName string     `json:"handlerName"` // 任务的处理器名称
	Params      string     `json:"params"`      // 任务的执行参数
	Sharding    int        `json:"sharding"`    // 广播执行任务的处理器分片参数
	CreateTime  *time.Time `json:"createTime"`  // 任务的创建时间
}

// RunnerManager 任务运行管理类接口
type RunnerManager interface {
	RunnerTask(task *models.Task, mode int, nodeId string) bool
	allotsTask(node *common.NodeItem, params *AllotsTaskParams)
}

type RunnerManagerImpl struct {
	recordService RecordService
	selector      TaskNodeSelect
	taskChannel   chan *taskRunner
}

func NewRunnerManager(recordService RecordService, selector TaskNodeSelect) RunnerManager {
	runner := &RunnerManagerImpl{
		recordService: recordService,
		selector:      selector,
		taskChannel:   make(chan *taskRunner, 10),
	}
	go handlerRunnerManagerChannel(runner)
	return runner
}

// RunnerTask 添加运行任务
func (r *RunnerManagerImpl) RunnerTask(task *models.Task, mode int, nodeId string) bool {
	if len(r.taskChannel) == cap(r.taskChannel) {
		return false
	}
	r.taskChannel <- &taskRunner{task, mode, nodeId}
	return true
}

// 下发任务到任务节点
func (r *RunnerManagerImpl) allotsTask(node *common.NodeItem, params *AllotsTaskParams) {
	address := "http://" + node.Address + "/task/issued"
	record := &models.Record{
		TaskId:         params.TaskId,
		ExecuteName:    node.Name,
		ExecuteAddress: node.Address,
		ExecuteParams:  params.Params,
		Status:         1,
	}
	err := r.recordService.SaveRecord(record)
	if err != nil {
		log.Errorf("添加任务运行记录失败，错误信息：%v\n", err)
		return
	}
	params.RecordId = record.ID

	requestBody, err := json.Marshal(params)
	if err != nil {
		log.Errorf("请求参数序列化失败，错误信息：%v\n", err)
		return
	}

	// 将任务通过Http请求下发到任务节点 如果任务节点的主地址下发失败再尝试使用备用地址进行下发
	// 主地址为任务节点访问服务端的IP地址 备用地址为任务节点上报的IP地址
	err = sendRequest(address, requestBody)
	if err != nil {
		log.Errorf("向任务节点主地址下发任务失败，错误信息：%v\n", err)
		suffix := address[strings.LastIndex(address, ":"):]
		reserveAddress := "http://" + node.Info.IpAddress + suffix
		log.Info("使用备用地址下发任务，备用地址：%v\n", reserveAddress)
		err = sendRequest(reserveAddress, requestBody)
		if err != nil {
			log.Errorf("备用地址下发任务失败，错误信息：%v ，取消任务下发\n", err)
			_ = r.recordService.UpdateRecord(&models.Record{
				ID:            params.RecordId,
				Status:        3,
				ResultContent: "任务下发失败",
			})
			return
		}
	}
	log.Info("任务下发成功")
	err = r.recordService.UpdateRecord(&models.Record{ID: params.RecordId, Status: 2})
	if err != nil {
		log.Errorf("更新任务状态失败，错误信息：%v\n", err)
	}
}

func sendRequest(uri string, body []byte) error {
	request, err := http.NewRequest(http.MethodPost, uri, bytes.NewBuffer(body))
	if err != nil {
		log.Errorf("生成Request对象失败，错误信息：%s\n", err)
		return err
	}
	request.Header.Set("Content-Type", "application/json")
	_, err = client.Do(request)
	if err != nil {
		log.Errorf("向任务节点下发任务失败，错误信息：%s\n", err)
		return err
	}
	return nil
}

func handlerRunnerManagerChannel(runner *RunnerManagerImpl) {
	for task := range runner.taskChannel {
		if task.TaskType == 1 {
			// 广播任务处理逻辑
			taskNodes := common.ListTaskNodeByStatus(0)
			for i := 0; i < len(taskNodes); i++ {
				node := taskNodes[i]
				// 针对任务节点分别下发每一个任务
				go runner.allotsTask(node, &AllotsTaskParams{
					TaskId:      task.ID,
					TaskType:    task.TaskType,
					HandlerType: task.HandlerType,
					HandlerName: task.HandlerName,
					Params:      task.Params,
					Sharding:    i + 1,
					CreateTime:  task.CreateTime,
				})
			}
		} else {
			// 单机任务处理逻辑
			selectNode := runner.selector.doSelect(task.mode, task.nodeId)
			go runner.allotsTask(selectNode, &AllotsTaskParams{
				TaskId:      task.ID,
				TaskType:    task.TaskType,
				HandlerType: task.HandlerType,
				HandlerName: task.HandlerName,
				Params:      task.Params,
				Sharding:    0,
				CreateTime:  task.CreateTime,
			})
		}
	}
}
