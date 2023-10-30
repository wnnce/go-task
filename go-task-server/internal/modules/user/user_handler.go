package user

import (
	"fmt"
	"github.com/gofiber/fiber/v2"
	"go-task-server/internal/models"
	"go-task-server/internal/utils"
	"log"
	"strconv"
)

type UserHandler struct {
	userService UserService
}

func NewUserHandler(userService UserService) *UserHandler {
	return &UserHandler{userService: userService}
}

func (u *UserHandler) Login(c *fiber.Ctx) error {
	user := new(models.User)
	if err := c.BodyParser(user); err != nil {
		log.Printf("请求参数反序列化错误，错误信息：%v\n", err)
		return utils.Fail(c, fmt.Errorf("请求参数错误"))
	}
	token, err := u.userService.Login(user.Name, user.Password, c.IP())
	if err != nil {
		return utils.FailRequest(c, err.Error())
	}
	return utils.Ok(c, token)
}

func (u *UserHandler) CreateUser(c *fiber.Ctx) error {
	user := new(models.User)
	if err := c.BodyParser(user); err != nil {
		log.Printf("请求参数反序列化错误，错误信息：%v\n", err)
		return utils.Fail(c, fmt.Errorf("请求参数错误"))
	}
	err := u.userService.AddUser(user.Name, user.Password, user.Remark)
	if err != nil {
		return utils.Fail(c, err)
	}
	return utils.OkMessage(c, "添加成功")
}

func (u *UserHandler) ListUser(c *fiber.Ctx) error {
	username := c.Query("name")
	pageString := c.Query("page", "1")
	sizeString := c.Query("size", "5")
	page, err := strconv.Atoi(pageString)
	if err != nil {
		return utils.Fail(c, fmt.Errorf("请求参数错误"))
	}
	size, err := strconv.Atoi(sizeString)
	if err != nil {
		return utils.Fail(c, fmt.Errorf("请求参数错误"))
	}
	pageResult, err := u.userService.ListUser(username, page, size)
	return utils.Ok(c, pageResult)
}
