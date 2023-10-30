package test

import (
	"errors"
	"fmt"
	"go-task-server/internal/common"
	"testing"
)

func TestCustomError(t *testing.T) {
	err := func() error {
		return common.NewCustomError(400, "自定义错误")
	}
	fmt.Printf("%v\n", err())
	var customError *common.CustomError
	if errors.As(err(), &customError) {
		fmt.Println(customError.Code())
		fmt.Println(customError.Message())
	}
}
