package task

import (
	"github.com/gofiber/fiber/v2"
	"log"
	"sync"
	"xorm.io/xorm"
)

func InitTaskModule(app *fiber.App, engine *xorm.Engine, wg *sync.WaitGroup) {
	wg.Add(1)
	defer wg.Done()
	log.Println("初始化任务模块")
	taskApi := app.Group("/task")
	repository := NewTaskRepository(engine)
	service := NewTaskService(repository)
	handler := NewTaskHandler(service)
	taskApi.Post("/", handler.AddTask)
	taskApi.Post("/list", handler.QueryTaskPage)
	taskApi.Get("/:id", handler.QueryTaskInfo)
	taskApi.Put("/", handler.UpdateTask)
	taskApi.Delete("/:id", handler.DeleteTaskById)
	taskApi.Put("/status", handler.UpdateTaskStatus)
}
