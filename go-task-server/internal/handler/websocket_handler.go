package handler

import (
	"github.com/gofiber/contrib/websocket"
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/common"
	"strconv"
	"strings"
	"time"
)

type WebSocketHandler struct {
}

func NewWebSocketHandler() *WebSocketHandler {
	return &WebSocketHandler{}
}

func (w *WebSocketHandler) NodeWebSocket(c *websocket.Conn) {
	clientId := c.Params("id")
	address := c.RemoteAddr().String()
	clientName := c.Query("name")
	intervalsStr := c.Query("intervals", "5")
	port := c.Query("port")
	intervals, err := strconv.ParseUint(intervalsStr, 10, 32)
	if len(clientName) <= 0 || err != nil {
		log.Errorf("任务节点连接参数错误，关闭连接,clientName：%v，err：%v\n", clientName, err)
		_ = c.Close()
		return
	}
	index := strings.LastIndex(address, ":")
	now := time.Now()
	address = address[:index] + ":" + port
	channel := make(chan int)
	client := &common.NodeItem{
		ID:         clientId,
		Name:       clientName,
		Intervals:  uint(intervals),
		Address:    address,
		OnlineTime: &now,
		Status:     0,
		Channel:    channel,
	}
	log.Infof("%v\n", client)
	log.Infof("任务节点连接成功，保存节点，clientId:%v\n", clientId)
	common.SaveTaskNode(clientId, client)
	clientInfo := &common.TaskNodeInfo{}

	go func(clientId string) {
		for {
			err = c.ReadJSON(clientInfo)
			if err != nil {
				log.Errorf("任务节点连接失败或消息解析失败，删除节点关闭连接，错误消息：%v\n", err)
				break
			}
			client = common.GetTaskNode(clientId)
			if client == nil {
				log.Error("节点信息被删除，上报信息无效，关闭连接")
				break
			}
			log.Debugf("任务节点上报信息续约，更新节点信息，clientId:%v\n", clientId)
			common.UpdateTaskNodeInfo(clientId, clientInfo)
			if err = c.WriteMessage(1, []byte("ok")); err != nil {
				log.Errorf("服务端返回消息失败，错误信息：%v\n", err)
			}
		}
		common.DeleteTaskNode(clientId)
	}(clientId)

	_ = <-channel
	log.Info("监听到关闭通道信息，关闭连接")
	_ = c.Close()
}

func (w *WebSocketHandler) ClientWebSocket(c *websocket.Conn) {
	log.Infof("客户端连接成功，IP：%v", c.RemoteAddr())
	for {
		_, p, err := c.ReadMessage()
		if err != nil {
			log.Error("读取客户端连接失败，关闭连接")
			_ = c.Close()
			break
		}
		log.Infof("%v", string(p))
	}
}
