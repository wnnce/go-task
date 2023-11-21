package common

import (
	"github.com/gofiber/contrib/websocket"
	"sync"
)

type Client struct {
	Con     *websocket.Conn // 客户端的WebSocket连接
	Channel chan int        // 客户端的Websocket关闭通道
}

var (
	clients  = make([]Client, 0)
	clientMu sync.Mutex
)
