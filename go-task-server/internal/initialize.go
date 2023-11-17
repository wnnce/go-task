package internal

import (
	"github.com/gofiber/contrib/websocket"
	"github.com/gofiber/fiber/v2"
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/handler"
	"go-task-server/internal/repository"
	"go-task-server/internal/service"
	"sync"
	"xorm.io/xorm"
)

func InitProject(app *fiber.App, engine *xorm.Engine) {
	log.Info("----------初始化repository----------")
	userRepository := repository.NewUserRepository(engine)
	taskRepository := repository.NewTaskRepository(engine)
	recordRepository := repository.NewRecordRepository(engine)
	logRepository := repository.NewTaskLogRepository(engine)
	log.Info("----------初始化service----------")
	userService := service.NewUserService(userRepository)
	taskService := service.NewTaskService(taskRepository)
	recordService := service.NewRecordService(recordRepository, logRepository)
	log.Info("----------初始化handler----------")
	userHandler := handler.NewUserHandler(userService)
	taskHandler := handler.NewTaskHandler(taskService)
	recordHandler := handler.NewRecordHandler(recordService)
	socketHandler := handler.NewWebSocketHandler()
	var wg sync.WaitGroup
	wg.Add(4)
	go initUserRoute(app, userHandler, &wg)
	go initTaskRoute(app, taskHandler, &wg)
	go initRecordRoute(app, recordHandler, &wg)
	go initWebSocketRoute(app, socketHandler, &wg)
	wg.Wait()
	log.Info("----------routers初始化完成----------")

}

func initUserRoute(app *fiber.App, handler *handler.UserHandler, wg *sync.WaitGroup) {
	defer wg.Done()
	log.Info("----------初始化User router----------")
	userApi := app.Group("/user")
	userApi.Post("/login", handler.Login)
	userApi.Post("/", handler.CreateUser)
	userApi.Get("/list", handler.ListUser)
	userApi.Delete("/:id", handler.DeleteUser)
}

func initTaskRoute(app *fiber.App, handler *handler.TaskHandler, wg *sync.WaitGroup) {
	wg.Done()
	log.Info("----------初始化Task router----------")
	taskApi := app.Group("/task")
	taskApi.Post("/", handler.AddTask)
	taskApi.Post("/list", handler.QueryTaskPage)
	taskApi.Get("/:id", handler.QueryTaskInfo)
	taskApi.Put("/", handler.UpdateTask)
	taskApi.Delete("/:id", handler.DeleteTaskById)
	taskApi.Put("/status", handler.UpdateTaskStatus)
	taskApi.Post("/report", handler.)
}

func initRecordRoute(app *fiber.App, handler *handler.RecordHandler, wg *sync.WaitGroup) {
	defer wg.Done()
	log.Info("----------初始化Record router----------")
	recordApi := app.Group("/record")
	recordApi.Post("/", handler.AddRecord)
	recordApi.Post("/list", handler.PageRecord)
	recordApi.Get("/:id", handler.RecordInfo)
	recordApi.Delete("/:id", handler.DeleteRecord)
	recordApi.Get("/logs/list/:id", handler.ListRecordLog)
	recordApi.Get("/logs/:id", handler.RecordLogInfo)
}

func initWebSocketRoute(app *fiber.App, handler *handler.WebSocketHandler, wg *sync.WaitGroup) {
	defer wg.Done()
	log.Info("----------初始化WebSocket router----------")
	app.Get("/client/registration/ws/:id", websocket.New(handler.WebSocket))
}
