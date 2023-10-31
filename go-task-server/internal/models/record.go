package models

import "time"

type Record struct {
	ID             int        `json:"id" xorm:"id pk"`
	TaskId         int        `json:"taskId"`
	ExecuteCount   uint       `json:"executeCount"`
	ExecuteName    string     `json:"executeName"`
	ExecuteAddress string     `json:"executeAddress"`
	ExecuteParams  string     `json:"executeParams"`
	CreateTime     *time.Time `json:"createTime" xorm:"created"'`
	RunnerTime     *time.Time `json:"runnerTime"`
	StopTime       *time.Time `json:"stopTime"`
	Status         uint       `json:"status"`
	ResultContent  string     `json:"resultContent"`
}

func (r *Record) TableName() string {
	return "xn_task_record"
}
