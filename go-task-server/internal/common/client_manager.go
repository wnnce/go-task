package common

import (
	"github.com/gofiber/fiber/v2/log"
	"sync"
	"time"
)

type ClientRegi struct {
	Name      string `json:"name"`      // 任务节点名称
	Port      uint   `json:"port"`      // 任务节点端口
	Intervals uint   `json:"intervals"` // 任务节点心跳时间间隔
}

type TaskClientInfo struct {
	UsedCpu     float64 `json:"UsedCpu"`     // CPU使用率
	TotalMemory int64   `json:"TotalMemory"` // 总内存
	UsedMemory  int64   `json:"usedMemory"`  // 使用内存
	TotalDisk   int64   `json:"totalDisk"`   // 总硬盘空间
	UsedDisk    int64   `json:"usedDisk"`    // 已使用内存空间
	IpAddress   string  `json:"ipAddress"`   // 实例IP地址
}

type ClientItem struct {
	ID         string     `json:"id"`         // 实例Id
	Name       string     `json:"name"`       // 实例名称
	Address    string     `json:"address"`    // 实例地址
	Intervals  uint       `json:"intervals"`  // 实例心跳间隔时间
	OnlineTime *time.Time `json:"onlineTime"` // 实例的最后在线时间
	Status     uint       `json:"status"`     // 0：正常 1：离线（超过心跳时间没有发送心跳请求
	Channel    chan int   // 关闭连接的通道
	roundFunc  func(string, uint)
	*TaskClientInfo
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
			if lastOnlineTime == nil {
				log.Error("客户端被删除，结束心跳检测")
				break
			}
			if time.Now().Sub(*lastOnlineTime) > maxTimeout {
				log.Warn("客户端心跳超时")
				retryCount++
				UpdateClientStatus(key, 1)
				if retryCount >= 3 {
					log.Warnf("客户端心跳超时3次，删除客户端，关闭连接，key:%v\n", key)
					DeleteClient(key)
					break
				}
			} else {
				log.Debugf("客户端心跳在线，key:%v, 最新时间：%v\n", key, lastOnlineTime)
				if retryCount > 0 {
					UpdateClientStatus(key, 0)
					retryCount = 0
				}
			}
		}
	}
	go client.roundFunc(key, client.Intervals)
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

func UpdateClientInfo(key string, info *TaskClientInfo) {
	mu.Lock()
	defer mu.Unlock()
	client, ok := clientManager[key]
	if !ok {
		log.Errorf("当前客户端不存在，key：%v\n", key)
		return
	}
	now := time.Now()
	client.TaskClientInfo = info
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
	client, ok := clientManager[key]
	if ok {
		log.Infof("删除客户端，key：%v\n", key)
		mu.Lock()
		close(client.Channel)
		delete(clientManager, key)
		mu.Unlock()
	}
}
