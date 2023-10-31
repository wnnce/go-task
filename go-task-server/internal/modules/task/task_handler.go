package task

import (
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/models"
	"go-task-server/internal/utils"
)

type TaskHandler struct {
	taskService TaskService
}

func NewTaskHandler(taskService TaskService) *TaskHandler {
	return &TaskHandler{
		taskService: taskService,
	}
}

func (t *TaskHandler) AddTask(c *fiber.Ctx) error {
	task := &models.Task{}
	err := c.BodyParser(task)
	if err != nil {
		return utils.FailRequest(c, "添加任务参数错误")
	}
	err = t.taskService.SaveTask(task)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}

func (t *TaskHandler) QueryTaskPage(c *fiber.Ctx) error {
	page := c.QueryInt("page", 1)
	size := c.QueryInt("size", 5)
	taskQuery := &models.TaskQuery{}
	err := c.BodyParser(taskQuery)
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	pageTask, err := t.taskService.PageTask(page, size, taskQuery)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, pageTask)
}

func (t *TaskHandler) QueryTaskInfo(c *fiber.Ctx) error {
	taskId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	taskInfo, err := t.taskService.QueryTaskInfo(taskId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, taskInfo)
}

func (t *TaskHandler) UpdateTask(c *fiber.Ctx) error {
	task := &models.Task{}
	err := c.BodyParser(task)
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	err = t.taskService.UpdateTask(task)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}

func (t *TaskHandler) DeleteTaskById(c *fiber.Ctx) error {
	taskId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	err = t.taskService.DeleteTaskById(taskId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}

func (t *TaskHandler) UpdateTaskStatus(c *fiber.Ctx) error {
	taskId := c.QueryInt("id")
	status := c.QueryInt("status")
	err := t.taskService.UpdateTaskStatus(taskId, status)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}
