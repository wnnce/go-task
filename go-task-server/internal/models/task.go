package models

import "time"

type Task struct {
	ID          int        `json:"id" xorm:"id pk"`
	Name        string     `json:"name"`
	Remark      string     `json:"remark,omitempty"`
	TaskType    int        `json:"taskType"`
	HandlerType int        `json:"handlerType"`
	HandlerName string     `json:"handlerName"`
	Params      string     `json:"params"`
	CreateTime  *time.Time `json:"createTime,omitempty" xorm:"created"`
	UpdateTime  *time.Time `json:"updateTime,omitempty" xorm:"updated"`
	Status      int        `json:"status"`
}

func (*Task) TableName() string {
	return "xn_task_info"
}
