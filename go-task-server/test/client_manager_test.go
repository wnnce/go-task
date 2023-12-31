package test

import (
	"fmt"
	"go-task-server/internal/common"
	"testing"
	"time"
)

func TestClientManager(t *testing.T) {
	client := &common.TaskClient{
		Name:      "测试",
		Address:   "127.0.0.1:31231",
		Cpu:       12.5,
		Memory:    23.7,
		Disk:      12.5,
		RoundTime: 4,
	}
	now := time.Now()
	clientItem := &common.NodeItem{
		ID:         "123",
		OnlineTime: &now,
		Status:     0,
		TaskClient: client,
	}
	common.SaveTaskNode("123", clientItem)
	cacheClient := common.GetTaskNode("123")
	if cacheClient != nil {
		fmt.Printf("%v\n", cacheClient)
	}
	cacheClient.Status = 1
	time.Sleep(time.Second * 10)
	now = time.Now()
	cacheClient = common.GetTaskNode("123")
	fmt.Printf("%v\n", cacheClient)
	common.DeleteTaskNode("123")
	cacheClient = common.GetTaskNode("123")
	fmt.Printf("%v\n", cacheClient)
}
