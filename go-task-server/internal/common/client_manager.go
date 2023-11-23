package common

import (
	"github.com/gofiber/contrib/websocket"
	"github.com/gofiber/fiber/v2/log"
	"sync"
	"time"
)

type Message struct {
	Code int         `json:"code"` // 发送给客户端的消息Code
	Data interface{} `json:"data"` // 发送给客户端的消息内容
}

// TaskNodeMessage 任务节点更新消息
type TaskNodeMessage struct {
	Id         string        `json:"id"`                   // 任务节点ID
	Status     uint          `json:"status,omitempty"`     // 任务节点状态
	OnlineTime *time.Time    `json:"onlineTime,omitempty"` // 任务节点的最后在线时间
	Info       *TaskNodeInfo `json:"info,omitempty"`       // 任务节点的信息
}

type Client struct {
	Con     *websocket.Conn // 客户端的WebSocket连接
	Channel *chan int       // 客户端的Websocket关闭通道
}

const (
	MessageCode = iota
	AddCode
	UpdateCode
	DeleteCode
)

var (
	clientCache = make(map[int64]*Client)
	clientMu    sync.RWMutex
)

// SaveClient 保存客户端连接
func SaveClient(clientId int64, con *websocket.Conn, channel *chan int) {
	clientMu.Lock()
	clientCache[clientId] = &Client{con, channel}
	clientMu.Unlock()
	nodes := listTaskNode()
	if len(nodes) > 0 {
		sendMessage(con, &Message{AddCode, nodes})
	}
}

// DeleteClient 删除客户端连接
func DeleteClient(clientId int64) {
	client, ok := clientCache[clientId]
	if ok {
		log.Infof("删除客户端，clientId：%d\n", clientId)
		clientMu.Lock()
		// 关闭客户端Channel
		close(*client.Channel)
		delete(clientCache, clientId)
		clientMu.Unlock()
	}
}

// 任务节点第一次连接时 向所有客户端推送信息
func pushAddTaskNode(node *NodeItem) {
	clients := listClient()
	if len(clients) <= 0 {
		log.Debug("没有客户端在线，放弃消息推送")
		return
	}
	for _, client := range clients {
		go sendMessage(client.Con, &Message{AddCode, [1]*NodeItem{node}})
	}
}

// 向保存的客户端连接推送更新的任务节点信息
func pushUpdateTaskNodeInfo(message *TaskNodeMessage) {
	clients := listClient()
	if len(clients) <= 0 {
		log.Debug("没有客户端在线，放弃消息推送")
		return
	}
	for _, client := range clients {
		go sendMessage(client.Con, &Message{UpdateCode, message})
	}
}

// 推送删除任务节点消息
func pushDeleteTaskNode(nodeId string) {
	clients := listClient()
	if len(clients) <= 0 {
		log.Debug("没有客户端在线，放弃消息推送")
		return
	}
	for _, client := range clients {
		go sendMessage(client.Con, &Message{DeleteCode, nodeId})
	}
}

// 通用发送消息方法
func sendMessage(conn *websocket.Conn, message *Message) {
	err := conn.WriteJSON(message)
	if err != nil {
		log.Errorf("向客户端发送消息失败，错误信息：%v", err)
	}
}

// 获取保存的所有客户端连接
func listClient() []*Client {
	clientMu.RLock()
	defer clientMu.RUnlock()
	clients := make([]*Client, 0, len(clientCache))
	for _, v := range clientCache {
		clients = append(clients, v)
	}
	return clients
}
