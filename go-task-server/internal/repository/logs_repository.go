package repository

import (
	"github.com/gofiber/fiber/v2/log"
	"go-task-server/internal/models"
	"xorm.io/xorm"
)

type TaskLogRepository interface {
	SaveTaskLog(taskLog *models.TaskLog) int64
	ListTaskLogByRecordId(recordId int) (*[]models.TaskLog, error)
	QueryTaskLogInfo(logId int) *models.TaskLog
	DeleteTaskLogByRecordId(recordId int) int64
}

type TaskLogRepositoryImpl struct {
	engine *xorm.Engine
}

func NewTaskLogRepository(engine *xorm.Engine) TaskLogRepository {
	return &TaskLogRepositoryImpl{
		engine: engine,
	}
}

func (t *TaskLogRepositoryImpl) SaveTaskLog(taskLog *models.TaskLog) int64 {
	insert, err := t.engine.Insert(taskLog)
	if err != nil {
		log.Errorf("日志添加失败，错误信息：%v\n", err)
	}
	return insert
}

func (t *TaskLogRepositoryImpl) ListTaskLogByRecordId(recordId int) (*[]models.TaskLog, error) {
	recordLogs := make([]models.TaskLog, 0)
	err := t.engine.Cols("id", "task_id", "record_id", "create_time").Where("record_id = ?", recordId).Find(&recordLogs)
	if err != nil {
		log.Errorf("查询日志列表失败，错误信息：%v\n", err)
		return nil, err
	}
	return &recordLogs, nil
}

func (t *TaskLogRepositoryImpl) QueryTaskLogInfo(logId int) *models.TaskLog {
	taskLog := &models.TaskLog{}
	result, err := t.engine.ID(logId).Get(taskLog)
	if err != nil {
		log.Errorf("查询日志详情失败，错误信息：%v\n", err)
	}
	if !result {
		return nil
	}
	return taskLog
}

func (t *TaskLogRepositoryImpl) DeleteTaskLogByRecordId(recordId int) int64 {
	result, err := t.engine.Where("record_id = ?", recordId).Delete(&models.TaskLog{})
	if err != nil {
		log.Errorf("删除日志失败，错误信息：%v，recordId：%d\n", err, recordId)
	}
	return result
}
