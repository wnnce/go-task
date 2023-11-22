package common

import (
	"github.com/gofiber/fiber/v2/log"
	"sync"
	"time"
)

type NodeRegi struct {
	Name      string `json:"name"`      // 任务节点名称
	Port      uint   `json:"port"`      // 任务节点端口
	Intervals uint   `json:"intervals"` // 任务节点心跳时间间隔
}

type TaskNodeInfo struct {
	UsedCpu     float64 `json:"usedCpu"`     // CPU使用率
	TotalMemory int64   `json:"totalMemory"` // 总内存
	UsedMemory  int64   `json:"usedMemory"`  // 使用内存
	TotalDisk   int64   `json:"totalDisk"`   // 总硬盘空间
	UsedDisk    int64   `json:"usedDisk"`    // 已使用内存空间
	IpAddress   string  `json:"ipAddress"`   // 实例IP地址
}

type NodeItem struct {
	ID         string     `json:"id"`         // 实例Id
	Name       string     `json:"name"`       // 实例名称
	Address    string     `json:"address"`    // 实例地址
	Intervals  uint       `json:"intervals"`  // 实例心跳间隔时间
	OnlineTime *time.Time `json:"onlineTime"` // 实例的最后在线时间
	Status     uint       `json:"status"`     // 0：正常 1：离线（超过心跳时间没有发送心跳请求
	Channel    *chan int  `json:"-"`          // 关闭连接的通道
	roundFunc  func(string, uint)
	Info       *TaskNodeInfo `json:"info"` // 任务节点的详细信息
}

var (
	taskNodeCache = make(map[string]*NodeItem)
	mu            sync.Mutex
)

func GetTaskNode(key string) *NodeItem {
	return taskNodeCache[key]
}

func SaveTaskNode(key string, client *NodeItem) {
	client.roundFunc = func(key string, roundTime uint) {
		maxTimeout := time.Second * time.Duration(roundTime)
		ticker := time.NewTicker(maxTimeout / 2)
		retryCount := 0
		for range ticker.C {
			lastOnlineTime := getTaskNodeOnlineTime(key)
			if lastOnlineTime == nil {
				log.Error("任务节点被删除，结束心跳检测")
				break
			}
			if time.Now().Sub(*lastOnlineTime) > maxTimeout {
				log.Warn("任务节点心跳超时")
				retryCount++
				UpdateTaskNodeStatus(key, 1)
				if retryCount >= 3 {
					log.Warnf("任务节点心跳超时3次，删除客户端，关闭连接，key:%v\n", key)
					DeleteTaskNode(key)
					break
				}
			} else {
				log.Debugf("任务节点心跳在线，key:%v, 最新时间：%v\n", key, lastOnlineTime)
				if retryCount > 0 {
					UpdateTaskNodeStatus(key, 0)
					retryCount = 0
				}
			}
		}
	}
	go client.roundFunc(key, client.Intervals)
	mu.Lock()
	defer mu.Unlock()
	taskNodeCache[key] = client
}

func getTaskNodeOnlineTime(key string) *time.Time {
	client := taskNodeCache[key]
	if client == nil {
		log.Errorf("当前节点不存在，key:%v\n", key)
		return nil
	}
	return client.OnlineTime
}

func UpdateTaskNodeInfo(key string, info *TaskNodeInfo) {
	mu.Lock()
	defer mu.Unlock()
	client, ok := taskNodeCache[key]
	if !ok {
		log.Errorf("当前节点不存在，key：%v\n", key)
		return
	}
	now := time.Now()
	client.Info = info
	client.OnlineTime = &now
	// 推送更新任务节点信息
	updateTaskNodeInfo(&TaskNodeMessage{
		Id:         key,
		OnlineTime: &now,
		Info:       info,
	})
}

func UpdateTaskNodeStatus(key string, status uint) {
	mu.Lock()
	defer mu.Unlock()
	client, ok := taskNodeCache[key]
	if !ok {
		log.Errorf("当前节点不存在，key：%v\n", key)
	}
	client.Status = status
	// 推送更新任务节点状态
	updateTaskNodeInfo(&TaskNodeMessage{
		Id:     key,
		Status: status,
	})
}

func DeleteTaskNode(key string) {
	client, ok := taskNodeCache[key]
	if ok {
		log.Infof("删除任务节点，key：%v\n", key)
		mu.Lock()
		close(*client.Channel)
		delete(taskNodeCache, key)
		mu.Unlock()
		deleteTaskNode(key)
	}
}

func listTaskNode() []*NodeItem {
	values := make([]*NodeItem, 0, len(taskNodeCache))
	for _, v := range taskNodeCache {
		values = append(values, v)
	}
	return values
}
