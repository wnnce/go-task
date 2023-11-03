package common

import (
	"github.com/gofiber/fiber/v2/log"
	"sync"
	"time"
)

type TaskClient struct {
	Name      string  `json:"name"`      // 实例名称
	Address   string  `json:"address"`   // 实例地址 ip:端口
	Cpu       float64 `json:"cpu"`       // cpu使用率
	Memory    float64 `json:"memory"`    // 内存使用率
	Disk      float64 `json:"disk"`      // 硬盘使用率
	RoundTime uint    `json:"roundTime"` // 上报时间间隔
}

type ClientItem struct {
	ID         string     `json:"id"`
	OnlineTime *time.Time `json:"onlineTime"` // 实例的最后在线时间
	Status     uint       `json:"status"`     // 0：正常 1：离线（超过心跳时间没有发送心跳请求
	roundFunc  func(string, uint)
	*TaskClient
}

var (
	clientManager = make(map[string]*ClientItem)
	mu            sync.Mutex
)

func GetClient(key string) *ClientItem {
	return clientManager[key]
}

func SaveClient(key string, client *ClientItem) {
	client.roundFunc = func(key string, roundTime uint) {
		maxTimeout := time.Second * time.Duration(roundTime)
		ticker := time.NewTicker(maxTimeout / 2)
		retryCount := 0
		for range ticker.C {
			lastOnlineTime := getClientOnlineTime(key)
			if time.Now().Sub(*lastOnlineTime) > maxTimeout {
				log.Warn("客户端心跳超时")
				retryCount++
				UpdateClientStatus(key, 1)
				if retryCount >= 3 {
					log.Errorf("客户端心跳超时3次，删除客户端，key:%v\n", key)
					DeleteClient(key)
					break
				}
			} else {
				log.Infof("客户端心跳在线，key:%v, 最新时间：%v\n", key, lastOnlineTime)
				if retryCount > 0 {
					UpdateClientStatus(key, 0)
					retryCount = 0
				}
			}
		}
	}
	go client.roundFunc(key, client.RoundTime)
	mu.Lock()
	defer mu.Unlock()
	clientManager[key] = client
}

func getClientOnlineTime(key string) *time.Time {
	client := clientManager[key]
	if client == nil {
		log.Errorf("当前客户端不存在，key:%v\n", key)
		return nil
	}
	return client.OnlineTime
}

func UpdateClientInfo(key string, info *TaskClient) {
	mu.Lock()
	defer mu.Unlock()
	client, ok := clientManager[key]
	if !ok {
		log.Errorf("当前客户端不存在，key：%v\n", key)
		return
	}
	now := time.Now()
	client.TaskClient = info
	client.OnlineTime = &now
}

func UpdateClientStatus(key string, status uint) {
	mu.Lock()
	defer mu.Unlock()
	client, ok := clientManager[key]
	if !ok {
		log.Errorf("当前客户端不存在，key：%v\n", key)
	}
	client.Status = status
}

func DeleteClient(key string) {
	mu.Lock()
	defer mu.Unlock()
	delete(clientManager, key)
}
