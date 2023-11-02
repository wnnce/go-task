package main

import (
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/log"
	_ "github.com/lib/pq"
	"go-task-server/internal"
	"go-task-server/internal/middlewares"
	"xorm.io/xorm"
)

func main() {
	dsn := "host=10.10.10.10 user=postgres password=admin dbname=task_server port=5432 sslmode=disable TimeZone=Asia/Shanghai"
	engine, err := xorm.NewEngine("postgres", dsn)
	if err != nil {
		log.Errorf("数据库连接失败，错误信息：%v\n", err)
	}
	app := fiber.New()
	app.Use(middlewares.AuthMiddleware)
	internal.InitProject(app, engine)
	log.SetLevel(log.LevelDebug)
	log.Fatal(app.Listen(":5400"))
}
