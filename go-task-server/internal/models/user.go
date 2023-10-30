package models

import "time"

type User struct {
	ID            uint       `json:"id" xorm:"id pk"`
	Name          string     `json:"name"`
	Password      string     `json:"password"`
	CreateTime    *time.Time `json:"createTime"`
	LastLoginTime *time.Time `json:"lastLoginTime,omitempty" xorm:"last_login_time updated"`
	LastLoginIp   string     `json:"lastLoginIp,omitempty"`
	Remark        string     `json:"remark"`
}

func (user *User) TableName() string {
	return "xn_user"
}
