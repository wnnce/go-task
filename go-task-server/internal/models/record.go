package models

import "time"

type Record struct {
	ID             int        `json:"id" xorm:"id pk"`
	TaskId         int        `json:"taskId"`
	ExecuteCount   uint       `json:"executeCount"`
	ExecuteName    string     `json:"executeName"`
	ExecuteAddress string     `json:"executeAddress"`
	ExecuteParams  string     `json:"executeParams,omitempty"`
	CreateTime     *time.Time `json:"createTime" xorm:"created"`
	RunnerTime     *time.Time `json:"runnerTime,o,omitempty"`
	StopTime       *time.Time `json:"stopTim,omitempty"`
	Status         uint       `json:"status"`
	ResultContent  string     `json:"resultContent,omitempty"`
}

func (r *Record) TableName() string {
	return "xn_task_record"
}

type TaskRecord struct {
	Record   `xorm:"extends"`
	TaskName string `json:"taskName" xorm:"task_name"`
}

func (*TaskRecord) TableName() string {
	return "xn_task_record"
}
