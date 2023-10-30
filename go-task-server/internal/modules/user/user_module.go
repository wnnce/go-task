package user

import (
	"github.com/gofiber/fiber/v2"
	"xorm.io/xorm"
)

func InitUserModule(app *fiber.App, engine *xorm.Engine) {
	userApi := app.Group("/user")
	userRepo := NewUserRepository(engine)
	userService := NewUserService(userRepo)
	userHandler := NewUserHandler(userService)
	userApi.Post("/login", userHandler.Login)
	userApi.Post("/", userHandler.CreateUser)
	userApi.Get("/list", userHandler.ListUser)
}
