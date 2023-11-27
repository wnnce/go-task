package service

import (
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"go-task-server/internal/repository"
)

type TaskService interface {
	SaveTask(task *models.Task) error
	PageTask(page, size int, query *models.TaskQuery) (*models.Page, error)
	QueryTaskInfo(taskId int) (*models.Task, error)
	UpdateTask(task *models.Task) error
	DeleteTaskById(taskId int) error
	UpdateTaskStatus(taskId, status int) error
	ExecuteTask(taskId, mode int, nodeId string) error
	HandlerTaskResult(result *models.TaskExecuteResult)
}

type TaskServiceImpl struct {
	taskRepo      repository.TaskRepository
	recordService RecordService
	runner        RunnerManager
}

func NewTaskService(taskRepo repository.TaskRepository, runner RunnerManager, recordService RecordService) TaskService {
	return &TaskServiceImpl{
		taskRepo:      taskRepo,
		runner:        runner,
		recordService: recordService,
	}
}

func (t *TaskServiceImpl) SaveTask(task *models.Task) error {
	result := t.taskRepo.SaveTask(task)
	if result <= 0 {
		return common.NewCustomError(500, "保存任务失败")
	}
	return nil
}

func (t *TaskServiceImpl) PageTask(page, size int, query *models.TaskQuery) (*models.Page, error) {
	pageTask, err := t.taskRepo.PageTask(page, size, query)
	if err != nil {
		return pageTask, common.NewCustomError(500, "查询用户列表失败")
	}
	return pageTask, nil
}

func (t *TaskServiceImpl) QueryTaskInfo(taskId int) (*models.Task, error) {
	taskInfo := t.taskRepo.QueryTaskInfo(taskId)
	if taskInfo == nil {
		return nil, common.NewCustomError(404, "任务不存在")
	}
	return taskInfo, nil
}

func (t *TaskServiceImpl) UpdateTask(task *models.Task) error {
	result := t.taskRepo.UpdateTask(task)
	if result <= 0 {
		return common.NewCustomError(500, "任务更新失败")
	}
	return nil
}

func (t *TaskServiceImpl) DeleteTaskById(taskId int) error {
	result := t.taskRepo.DeleteTaskById(taskId)
	if result <= 0 {
		return common.NewCustomError(500, "任务删除失败")
	}
	return nil
}

func (t *TaskServiceImpl) UpdateTaskStatus(taskId, status int) error {
	result := t.taskRepo.UpdateTaskStatus(taskId, status)
	if result <= 0 {
		return common.NewCustomError(500, "更新任务状态失败")
	}
	return nil
}

func (t *TaskServiceImpl) ExecuteTask(taskId, mode int, nodeId string) error {
	taskInfo, err := t.QueryTaskInfo(taskId)
	if err != nil {
		return err
	}
	if mode == 1 {
		taskNode := common.GetTaskNode(nodeId)
		if taskNode == nil {
			return common.NewCustomError(400, "任务节点不存在")
		}
		if taskNode.Status == 1 {
			return common.NewCustomError(400, "任务节点状态异常")
		}
	}
	ok := t.runner.RunnerTask(taskInfo, mode, nodeId)
	if !ok {
		return common.NewCustomError(500, "任务运行失败，超出任务缓存的最大容量")
	}
	return nil
}

func (t *TaskServiceImpl) HandlerTaskResult(result *models.TaskExecuteResult) {
	status := 0
	if result.Status == 1 {
		status = 3
	}
	record := &models.Record{
		ID:            result.RecordId,
		Status:        uint(status),
		RunnerTime:    result.RunnerTime,
		StopTime:      result.ClosingTime,
		ResultContent: result.Outcome,
	}
	err := t.recordService.UpdateRecord(record)
	if err != nil {
		log.Error("更新任务运行记录失败，放弃日志写入")
	} else {
		// 没有日志信息就不保存空日志
		if len(result.RunnerLogs) <= 0 {
			return
		}
		resultLog := &models.TaskLog{
			TaskId:   result.TaskId,
			RecordId: result.RecordId,
			Content:  result.RunnerLogs,
		}
		err = t.recordService.AddRecordLog(resultLog)
		if err != nil {
			log.Error("添加任务运行日志失败")
		}
	}
}
