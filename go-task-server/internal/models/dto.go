package models

import "time"

type Page struct {
	Page      int         `json:"page"`
	Size      int         `json:"size"`
	Total     int64       `json:"total"`
	TotalPage int         `json:"totalPage"`
	List      interface{} `json:"list"`
}

type TaskQuery struct {
	Name        string `json:"name"`
	HandlerType int    `json:"handlerType"`
	TaskType    int    `json:"taskType"`
	StartTime   string `json:"startTime"`
	EndTime     string `json:"endTime"`
}

type RecordQuery struct {
	TaskId int `json:"taskId"`
	Status int `json:"status"`
}

// ExecuteDto 提交任务运行参数Dto类
type ExecuteDto struct {
	TaskId int    `json:"TaskId"` // 需要运行的任务ID
	Mode   int    `json:"mode"`   // 任务运行的调度模式 0：默认调度（轮询） 1：指定节点运行
	NodeId string `json:"nodeId"` // Mode参数为1时，指定任务节点的 节点ID
}

type TaskExecuteResult struct {
	TaskId      int        `json:"taskId"`      // 运行的任务Id
	RecordId    int        `json:"recordId"`    // 运行记录Id
	RunnerTime  *time.Time `json:"runnerTime"`  // 任务的运行时间
	ClosingTime *time.Time `json:"closingTime"` // 任务的运行结束时间
	Status      int        `json:"status"`      // 运行状态 0：成功 1：失败
	Outcome     string     `json:"outcome"`     // 返回的任务运行结果
	RunnerLogs  string     `json:"runnerLogs"`  // 任务运行的日志记录
	NodeDemo    string     `json:"nodeDemo"`    // 运行任务的任务节点名称
}

type CountInfo struct {
	TaskCount       int `json:"taskCount" xorm:"task_count"`              // 任务总数
	FailTaskCount   int `json:"failTaskCount" xorm:"fail_task_count"`     // 近期失败任务数
	RunnerNodeCount int `json:"runnerNodeCount" xorm:"runner_node_count"` // 正在运行实例数
}
