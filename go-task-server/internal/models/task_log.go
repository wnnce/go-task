package models

import "time"

type TaskLog struct {
	ID         int        `json:"id" xorm:"id pk autoincr"`
	TaskId     int        `json:"taskId"`
	RecordId   int        `json:"recordId"`
	Content    string     `json:"content"`
	CreateTime *time.Time `json:"createTime" xorm:"created"`
}

func (l *TaskLog) TableName() string {
	return "xn_task_logs"
}
