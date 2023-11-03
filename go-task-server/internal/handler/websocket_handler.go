package handler

import (
	"github.com/gofiber/contrib/websocket"
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/common"
	"time"
)

type WebSocketHandler struct {
}

func NewWebSocketHandler() *WebSocketHandler {
	return &WebSocketHandler{}
}

func (w *WebSocketHandler) WebSocket(c *websocket.Conn) {
	clientId := c.Params("id")
	clientInfo := &common.TaskClient{}
	for {
		err := c.ReadJSON(clientInfo)
		if err != nil {
			log.Errorf("客户端连接失败或消息解析失败，删除客户端关闭连接，错误消息：%v\n", err)
			common.DeleteClient(clientId)
			_ = c.Close()
			break
		}
		client := common.GetClient(clientId)
		if client == nil {
			now := time.Now()
			client = &common.ClientItem{
				ID:         clientId,
				OnlineTime: &now,
				Status:     0,
				TaskClient: clientInfo,
			}
			log.Infof("第一次连接，添加客户端，clientId:%v\n", clientId)
			common.SaveClient(clientId, client)
		} else {
			log.Infof("客户端上报信息续约，更新客户端信息，clientId:%v\n", clientId)
			common.UpdateClientInfo(clientId, clientInfo)
		}
		if err = c.WriteMessage(1, []byte("ok")); err != nil {
			log.Errorf("服务端返回消息失败，错误信息：%v\n", err)
		}
	}
}
