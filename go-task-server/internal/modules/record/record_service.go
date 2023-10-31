package record

import (
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"log"
)

type RecordService interface {
	SaveRecord(record *models.Record) error
	PageRecord(page, size int, query *models.RecordQuery) (*models.Page, error)
	QueryRecordInfo(recordId int) (*models.Record, error)
	DeleteRecord(recordId int) error
	ListRecordLog(recordId int) (*[]models.TaskLog, error)
	QueryRecordLogInfo(logId int) (*models.TaskLog, error)
}

type RecordServiceImpl struct {
	recordRepo RecordRepository
	logRepo    TaskLogRepository
}

func NewRecordService(recordRepo RecordRepository, logRepo TaskLogRepository) RecordService {
	return &RecordServiceImpl{
		recordRepo: recordRepo,
		logRepo:    logRepo,
	}
}

func (r RecordServiceImpl) SaveRecord(record *models.Record) error {
	result := r.recordRepo.SaveTaskRecord(record)
	if result <= 0 {
		return common.NewCustomError(500, "添加运行记录失败")
	}
	return nil
}

func (r RecordServiceImpl) PageRecord(page, size int, query *models.RecordQuery) (*models.Page, error) {
	record, err := r.recordRepo.PageRecord(page, size, query)
	if err != nil {
		return nil, common.NewCustomError(500, "查询运行记录失败")
	}
	return record, nil
}

func (r RecordServiceImpl) QueryRecordInfo(recordId int) (*models.Record, error) {
	record := r.recordRepo.QueryRecordInfo(recordId)
	if record == nil {
		return nil, common.NewCustomError(404, "运行记录不存在")
	}
	return record, nil
}

func (r RecordServiceImpl) DeleteRecord(recordId int) error {
	result := r.recordRepo.DeleteRecord(recordId)
	if result <= 0 {
		return common.NewCustomError(500, "删除运行记录失败")
	}
	// 异步线程删除该运行记录的所有日志
	go func() {
		result := r.logRepo.DeleteTaskLogByRecordId(recordId)
		log.Printf("删除运行记录日志完成，删除条数：%d\n", result)
	}()
	return nil
}

func (r RecordServiceImpl) ListRecordLog(recordId int) (*[]models.TaskLog, error) {
	taskLogs, err := r.logRepo.ListTaskLogByRecordId(recordId)
	if err != nil {
		return nil, common.NewCustomError(500, "获取运行记录日志失败")
	}
	return taskLogs, nil
}

func (r RecordServiceImpl) QueryRecordLogInfo(logId int) (*models.TaskLog, error) {
	taskLogInfo := r.logRepo.QueryTaskLogInfo(logId)
	if taskLogInfo == nil {
		return nil, common.NewCustomError(404, "日志不存在")
	}
	return taskLogInfo, nil
}
