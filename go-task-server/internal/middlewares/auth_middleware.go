package middlewares

import (
	"github.com/gofiber/contrib/websocket"
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/utils"
)

var openUris = []string{
	"/user/login",
	"/task/report",
}

// AuthMiddleware 验证用户是否登陆认证的中间件函数 从请求中获取Bearer Token然后验证Token是否有效。
// 如果有效则放行请求 无效返回401响应码和错误信息
func AuthMiddleware(c *fiber.Ctx) error {
	uri := c.OriginalURL()
	for _, value := range openUris {
		if value == uri {
			return c.Next()
		}
	}
	// 放行websocket请求
	if websocket.IsWebSocketUpgrade(c) {
		return c.Next()
	}
	authorization := c.Get("Authorization")
	if len(authorization) > 7 {
		token := authorization[7:]
		if utils.ParseToken(token) {
			return c.Next()
		}
	}
	return utils.FailAuth(c, "没有权限")
}
