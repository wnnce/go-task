package models

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
