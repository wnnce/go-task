package main

import (
	"github.com/gofiber/fiber/v2"
	_ "github.com/lib/pq"
	"go-task-server/internal/middlewares"
	"go-task-server/internal/modules/user"
	"log"
	"xorm.io/xorm"
)

func main() {
	dsn := "host=10.10.10.10 user=postgres password=admin dbname=task_server port=5432 sslmode=disable TimeZone=Asia/Shanghai"
	engine, err := xorm.NewEngine("postgres", dsn)
	if err != nil {
		log.Printf("数据库连接失败，错误信息：%v\n", err)
	}
	app := fiber.New()
	app.Use(middlewares.AuthMiddleware)
	user.InitUserModule(app, engine)
	log.Fatal(app.Listen(":5400"))
}
