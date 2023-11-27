package models

import "time"

type Record struct {
	ID             int        `json:"id" xorm:"id pk autoincr"`  // 运行记录ID
	TaskId         int        `json:"taskId"`                    // 运行记录对应的任务ID
	ExecuteCount   uint       `json:"executeCount"`              // 任务的重试次数
	ExecuteName    string     `json:"executeName"`               // 执行任务的任务节点名称
	ExecuteAddress string     `json:"executeAddress"`            // 执行任务的任务节点地址
	ExecuteParams  string     `json:"executeParams,omitempty"`   // 任务的执行参数
	CreateTime     *time.Time `json:"createTime" xorm:"created"` // 运行记录的创建时间
	RunnerTime     *time.Time `json:"runnerTime,o,omitempty"`    // 任务的运行时间
	StopTime       *time.Time `json:"stopTime,omitempty"`        // 任务的完成时间
	Status         uint       `json:"status"`                    // 任务的运行状态
	ResultContent  string     `json:"resultContent,omitempty"`   // 任务返回的结果内容
}

func (r *Record) TableName() string {
	return "xn_task_record"
}

type TaskRecord struct {
	*Record  `xorm:"extends"`
	TaskName string `json:"taskName" xorm:"task_name"`
}

func (*TaskRecord) TableName() string {
	return "xn_task_record"
}
