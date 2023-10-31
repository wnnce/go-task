package user

import (
	"github.com/gofiber/fiber/v2"
	"log"
	"sync"
	"xorm.io/xorm"
)

func InitUserModule(app *fiber.App, engine *xorm.Engine, wg *sync.WaitGroup) {
	wg.Add(1)
	defer wg.Done()
	log.Println("初始化用户模块")
	userApi := app.Group("/user")
	userRepo := NewUserRepository(engine)
	userService := NewUserService(userRepo)
	userHandler := NewUserHandler(userService)
	userApi.Post("/login", userHandler.Login)
	userApi.Post("/", userHandler.CreateUser)
	userApi.Get("/list", userHandler.ListUser)
}
