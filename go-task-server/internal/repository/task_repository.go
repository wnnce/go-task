package repository

import (
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/models"
	"math"
	"xorm.io/xorm"
)

type TaskRepository interface {
	SaveTask(task *models.Task) int64
	PageTask(page, size int, query *models.TaskQuery) (*models.Page, error)
	QueryTaskInfo(taskId int) *models.Task
	UpdateTask(task *models.Task) int64
	DeleteTaskById(taskId int) int64
	UpdateTaskStatus(taskId, status int) int64
	QueryCountInfo() *models.CountInfo
}

type TaskRepositoryImpl struct {
	engine *xorm.Engine
}

func NewTaskRepository(engine *xorm.Engine) TaskRepository {
	return &TaskRepositoryImpl{
		engine: engine,
	}
}

func (t *TaskRepositoryImpl) SaveTask(task *models.Task) int64 {
	insert, err := t.engine.Cols("name", "remark", "task_type", "handler_type", "handler_name", "params").Insert(task)
	if err != nil || insert <= 0 {
		log.Errorf("保存用户信息失败，错误信息：%v\n", err)
	}
	return insert
}

func (t *TaskRepositoryImpl) PageTask(page, size int, query *models.TaskQuery) (*models.Page, error) {
	session := t.engine.Cols("id")
	t.handlerPageSession(session, query)
	total, _ := session.Count(&models.Task{})
	if total <= 0 {
		return &models.Page{}, nil
	}
	maxPage := int(math.Ceil(float64(total) / float64(size)))
	if size > 100 {
		size = 100
	}
	var offset int
	if page > maxPage {
		offset = (maxPage - 1) * size
	} else {
		offset = (page - 1) * size
	}
	listSession := t.engine.Cols("id", "name", "task_type", "handler_type", "create_time", "status")
	t.handlerPageSession(listSession, query)
	tasks := make([]models.Task, 0)
	err := listSession.Limit(size, offset).Find(&tasks)
	if err != nil {
		log.Errorf("查询任务列表失败，错误信息：%v\n", err)
		return nil, err
	}
	return &models.Page{
		Page:      page,
		Size:      size,
		Total:     total,
		TotalPage: maxPage,
		List:      &tasks,
	}, nil
}

func (t *TaskRepositoryImpl) QueryTaskInfo(taskId int) *models.Task {
	task := new(models.Task)
	result, err := t.engine.ID(taskId).Get(task)
	if err != nil {
		log.Errorf("查询任务详情失败，错误信息：%v\n", err)
	}
	if !result {
		return nil
	}
	return task
}

func (t *TaskRepositoryImpl) UpdateTask(task *models.Task) int64 {
	update, err := t.engine.ID(task.ID).Cols("name", "remark", "task_type", "handler_type", "handler_name", "params").Update(task)
	if err != nil {
		log.Errorf("更新任务信息失败，错误信息：%v\n", err)
	}
	return update
}

func (t *TaskRepositoryImpl) DeleteTaskById(taskId int) int64 {
	result, err := t.engine.ID(taskId).Delete(&models.Task{})
	if err != nil {
		log.Errorf("删除任务失败，错误信息：%v\n", err)
	}
	return result
}

func (t *TaskRepositoryImpl) UpdateTaskStatus(taskId, status int) int64 {
	task := &models.Task{
		Status: status,
	}
	update, err := t.engine.ID(taskId).Cols("status").Update(task)
	if err != nil {
		log.Errorf("更新任务状态失败，错误信息：%v\n", err)
	}
	return update
}

func (t *TaskRepositoryImpl) handlerPageSession(session *xorm.Session, query *models.TaskQuery) {
	session.Where("id > ?", 0)
	if len(query.Name) > 0 {
		session.And("name like ?", "%"+query.Name+"%")
	}
	if query.HandlerType > 0 && query.HandlerType < 4 {
		session.And("handler_type = ?", query.HandlerType-1)
	}
	if query.TaskType > 0 && query.TaskType < 3 {
		session.And("task_type = ?", query.TaskType-1)
	}
	if len(query.StartTime) > 0 {
		session.And("create_time > ?", query.StartTime)
	}
	if len(query.EndTime) > 0 {
		session.And("create_time < ?", query.EndTime)
	}
}

func (t *TaskRepositoryImpl) QueryCountInfo() *models.CountInfo {
	count := &models.CountInfo{}
	result, err := t.engine.SQL("select * from (select count(id) as task_count from xn_task_info) as t1, (select count(id) as fail_task_count from xn_task_record where status = 3) as r1, (select count(distinct execute_address) as runner_node_count from xn_task_record where status = 2) as r2").Get(count)
	if err != nil {
		log.Errorf("查询数据库失败，错误信息：%v\n", err)
	}
	if !result {
		return nil
	}
	return count

}
