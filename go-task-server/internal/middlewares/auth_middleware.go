package middlewares

import (
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/utils"
)

// AuthMiddleware 验证用户是否登陆认证的中间件函数 从请求中获取Bearer Token然后验证Token是否有效。
// 如果有效则放行请求 无效返回401响应码和错误信息
func AuthMiddleware(c *fiber.Ctx) error {
	if c.OriginalURL() == "/user/login" {
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
