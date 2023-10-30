package utils

import (
	"errors"
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/common"
	"time"
)

type Result struct {
	Code      int         `json:"code,omitempty"`
	Message   string      `json:"message,omitempty"`
	Timestamp int64       `json:"timestamp,omitempty"`
	Data      interface{} `json:"data,omitempty"`
}

var customError *common.CustomError

func Ok(c *fiber.Ctx, data interface{}) error {
	return c.JSON(&Result{
		Code:      200,
		Message:   "ok",
		Timestamp: time.Now().UnixMilli(),
		Data:      data,
	})
}

func OkMessage(c *fiber.Ctx, message string) error {
	return c.JSON(&Result{
		Code:      200,
		Message:   message,
		Timestamp: time.Now().UnixMilli(),
	})
}

func Fail(c *fiber.Ctx, err error) error {
	var code int
	var message string
	if errors.As(err, &customError) {
		code = customError.Code()
		message = customError.Message()
	} else {
		code = 400
		message = err.Error()
	}
	return c.JSON(&Result{
		Code:      code,
		Message:   message,
		Timestamp: time.Now().UnixMilli(),
	})
}

func FailAuth(c *fiber.Ctx, message string) error {
	c.Status(401)
	return c.JSON(&Result{
		Code:      401,
		Message:   message,
		Timestamp: time.Now().UnixMilli(),
	})
}

func FailRequest(c *fiber.Ctx, message string) error {
	c.Status(400)
	return c.JSON(&Result{
		Code:      400,
		Message:   message,
		Timestamp: time.Now().UnixMilli(),
	})
}

func FailServer(c *fiber.Ctx, message string) error {
	c.Status(500)
	return c.JSON(&Result{
		Code:      500,
		Message:   message,
		Timestamp: time.Now().UnixMilli(),
	})
}
