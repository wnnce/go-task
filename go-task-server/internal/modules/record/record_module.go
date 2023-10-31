package record

import (
	"github.com/gofiber/fiber/v2"
	"log"
	"sync"
	"xorm.io/xorm"
)

func InitRecordModule(app *fiber.App, engine *xorm.Engine, wg *sync.WaitGroup) {
	wg.Add(1)
	defer wg.Done()
	log.Println("初始化运行记录模块")
	recordRepository := NewRecordRepository(engine)
	logRepository := NewTaskLogRepository(engine)
	recordService := NewRecordService(recordRepository, logRepository)
	recordHandler := NewRecordHandler(recordService)
	recordApi := app.Group("/record")
	recordApi.Post("/", recordHandler.AddRecord)
	recordApi.Post("/list", recordHandler.PageRecord)
	recordApi.Get("/:id", recordHandler.RecordInfo)
	recordApi.Delete("/:id", recordHandler.DeleteRecord)
	recordApi.Get("/logs/list/:id", recordHandler.ListRecordLog)
	recordApi.Get("/logs/:id", recordHandler.RecordLogInfo)
}
