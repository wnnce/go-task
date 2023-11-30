package main

import (
	"fmt"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/log"
	_ "github.com/lib/pq"
	"go-task-server/internal"
	"go-task-server/internal/config"
	"go-task-server/internal/middlewares"
	"xorm.io/xorm"
)

func main() {
	conf := config.GetConfig()
	if conf == nil {
		log.Fatal("读取配置文件失败")
	}
	dsn := fmt.Sprintf(
		"host=%s user=%s password=%s dbname=%s port=%d sslmode=disable TimeZone=Asia/Shanghai",
		conf.Database.Host,
		conf.Database.Username,
		conf.Database.Password,
		conf.Database.Dbname,
		conf.Database.Port,
	)
	engine, err := xorm.NewEngine("postgres", dsn)
	if err != nil {
		log.Fatal("数据库连接失败，错误信息：%v\n", err)
	}
	app := fiber.New()
	app.Use(middlewares.AuthMiddleware)
	internal.InitProject(app, engine)
	log.SetLevel(log.LevelInfo)
	log.Fatal(app.Listen(fmt.Sprintf(":%d", conf.Server.Port)))
}
