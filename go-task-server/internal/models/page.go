package models

type Page struct {
	Page      int         `json:"page"`
	Size      int         `json:"size"`
	Total     int64       `json:"total"`
	TotalPage int         `json:"totalPage"`
	List      interface{} `json:"list"`
}
