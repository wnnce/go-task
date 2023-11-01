package record

import (
	"go-task-server/internal/models"
	"log"
	"math"
	"xorm.io/xorm"
)

type RecordRepository interface {
	SaveTaskRecord(record *models.Record) int64
	PageRecord(page, size int, query *models.RecordQuery) (*models.Page, error)
	QueryRecordInfo(recordId int) *models.TaskRecord
	DeleteRecord(recordId int) int64
}

type RecordRepositoryImpl struct {
	engine *xorm.Engine
}

func NewRecordRepository(engine *xorm.Engine) RecordRepository {
	return &RecordRepositoryImpl{
		engine: engine,
	}
}

func (r *RecordRepositoryImpl) SaveTaskRecord(record *models.Record) int64 {
	insert, err := r.engine.Cols("task_id", "execute_name", "execute_params", "execute_address").Insert(record)
	if err != nil {
		log.Printf("保存运行记录失败，错误信息：%v\n", err)
	}
	return insert
}

func (r *RecordRepositoryImpl) PageRecord(page, size int, query *models.RecordQuery) (*models.Page, error) {
	session := r.engine.Cols("id")
	r.handlerPageSession(session, query)
	total, _ := session.Count(&models.Record{})
	if total <= 0 {
		return &models.Page{}, nil
	}
	if size > 100 {
		size = 100
	}
	maxPage := int(math.Ceil(float64(total) / float64(size)))
	var offset int
	if page > maxPage {
		offset = (maxPage - 1) * size
	} else {
		offset = (page - 1) * size
	}
	listSession := r.engine.SQL("select r.id, r.task_id, r.execute_count, r.execute_name, r.create_time, r.stop_time, r.status, t.name as task_name from xn_task_record as r left join xn_task_info as t on t.id = r.task_id")
	r.handlerPageSession(listSession, query)
	records := make([]models.TaskRecord, 0)
	err := listSession.Limit(size, offset).Find(&records)
	if err != nil {
		log.Printf("查询运行记录失败，错误信息：%v\n", err)
		return nil, err
	}
	return &models.Page{
		Page:      page,
		Size:      size,
		Total:     total,
		TotalPage: maxPage,
		List:      &records,
	}, nil
}

func (r *RecordRepositoryImpl) QueryRecordInfo(recordId int) *models.TaskRecord {
	record := &models.TaskRecord{}
	result, err := r.engine.
		SQL("select r.*, t.name as task_name from xn_task_record as r left join xn_task_info as t on t.id = r.task_id").
		Where("id = ?", record).
		Get(record)
	if err != nil {
		log.Printf("获取记录详情失败，错误信息：%v\n", err)
	}
	if !result {
		return nil
	}
	return record
}

func (r *RecordRepositoryImpl) DeleteRecord(recordId int) int64 {
	result, err := r.engine.ID(recordId).Delete(&models.Record{})
	if err != nil {
		log.Printf("删除记录失败，错误信息：%v\n", err)
	}
	return result
}

func (r *RecordRepositoryImpl) handlerPageSession(session *xorm.Session, query *models.RecordQuery) {
	session.Where("id > ?", 0)
	if query.TaskId != 0 {
		session.And("id = ?", query.TaskId)
	}
	if query.Status != 0 {
		session.And("status = ?", query.Status-1)
	}
}
