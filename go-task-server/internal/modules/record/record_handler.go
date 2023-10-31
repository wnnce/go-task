package record

import (
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/models"
	"go-task-server/internal/utils"
)

type RecordHandler struct {
	recordService RecordService
}

func NewRecordHandler(recordService RecordService) *RecordHandler {
	return &RecordHandler{
		recordService: recordService,
	}
}

func (r *RecordHandler) AddRecord(c *fiber.Ctx) error {
	record := &models.Record{}
	err := c.BodyParser(record)
	if err != nil {
		return utils.FailRequest(c, "添加运行记录参数错误")
	}
	err = r.recordService.SaveRecord(record)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}

func (r *RecordHandler) PageRecord(c *fiber.Ctx) error {
	page := c.QueryInt("page", 1)
	size := c.QueryInt("size", 5)
	query := &models.RecordQuery{}
	err := c.BodyParser(query)
	if err != nil {
		return utils.FailRequest(c, "查询运行记录参数错误")
	}
	pageRecord, err := r.recordService.PageRecord(page, size, query)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, pageRecord)

}
func (r *RecordHandler) RecordInfo(c *fiber.Ctx) error {
	recordId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	info, err := r.recordService.QueryRecordInfo(recordId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, info)
}

func (r *RecordHandler) DeleteRecord(c *fiber.Ctx) error {
	recordId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	err = r.recordService.DeleteRecord(recordId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, nil)
}

func (r *RecordHandler) ListRecordLog(c *fiber.Ctx) error {
	recordId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误")
	}
	recordLogs, err := r.recordService.ListRecordLog(recordId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, recordLogs)
}

func (r *RecordHandler) RecordLogInfo(c *fiber.Ctx) error {
	logId, err := c.ParamsInt("id")
	if err != nil {
		return utils.FailRequest(c, "请求参数错误哦")
	}
	logInfo, err := r.recordService.QueryRecordLogInfo(logId)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.Ok(c, logInfo)
}
