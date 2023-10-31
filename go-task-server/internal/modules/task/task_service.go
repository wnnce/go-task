package task

import (
	"go-task-server/internal/common"
	"go-task-server/internal/models"
)

type TaskService interface {
	SaveTask(task *models.Task) error
	PageTask(page, size int, query *models.TaskQuery) (*models.Page, error)
	QueryTaskInfo(taskId int) (*models.Task, error)
	UpdateTask(task *models.Task) error
	DeleteTaskById(taskId int) error
	UpdateTaskStatus(taskId, status int) error
}

type TaskServiceImpl struct {
	taskRepo TaskRepository
}

func NewTaskService(taskRepo TaskRepository) TaskService {
	return &TaskServiceImpl{
		taskRepo: taskRepo,
	}
}

func (t *TaskServiceImpl) SaveTask(task *models.Task) error {
	result := t.taskRepo.SaveTask(task)
	if result <= 0 {
		return common.NewCustomError(500, "保存任务失败")
	}
	return nil
}

func (t *TaskServiceImpl) PageTask(page, size int, query *models.TaskQuery) (*models.Page, error) {
	pageTask, err := t.taskRepo.PageTask(page, size, query)
	if err != nil {
		return pageTask, common.NewCustomError(500, "查询用户列表失败")
	}
	return pageTask, nil
}

func (t *TaskServiceImpl) QueryTaskInfo(taskId int) (*models.Task, error) {
	taskInfo := t.taskRepo.QueryTaskInfo(taskId)
	if taskInfo == nil {
		return nil, common.NewCustomError(404, "任务不存在")
	}
	return taskInfo, nil
}

func (t *TaskServiceImpl) UpdateTask(task *models.Task) error {
	result := t.taskRepo.UpdateTask(task)
	if result <= 0 {
		return common.NewCustomError(500, "任务更新失败")
	}
	return nil
}

func (t *TaskServiceImpl) DeleteTaskById(taskId int) error {
	result := t.taskRepo.DeleteTaskById(taskId)
	if result <= 0 {
		return common.NewCustomError(500, "任务删除失败")
	}
	return nil
}

func (t *TaskServiceImpl) UpdateTaskStatus(taskId, status int) error {
	result := t.taskRepo.UpdateTaskStatus(taskId, status)
	if result <= 0 {
		return common.NewCustomError(500, "更新任务状态失败")
	}
	return nil
}
