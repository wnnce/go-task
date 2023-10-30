package user

import (
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"go-task-server/internal/utils"
)

type UserService interface {
	Login(username, password, ip string) (string, error)
	AddUser(username, password, remark string) error
	ListUser(username string, page, size int) (*models.Page, error)
}

type UserServiceImpl struct {
	userRepo UserRepository
}

func NewUserService(userRepo UserRepository) UserService {
	return &UserServiceImpl{userRepo: userRepo}
}

func (u *UserServiceImpl) Login(username, password, ip string) (string, error) {
	findUser := u.userRepo.Login(username, password)
	if findUser == nil {
		return "", common.NewCustomError(400, "用户名或密码错误")
	}
	// 异步线程更新用户最后登陆信息
	go u.userRepo.UpdateLastIpById(findUser.ID, ip)
	token := utils.GenerateToken(findUser.ID, findUser.Name)
	return token, nil
}

func (u *UserServiceImpl) AddUser(username, password, remark string) error {
	result := u.userRepo.ExistUserByName(username)
	if result {
		return common.NewCustomError(400, "用户名已经存在")
	}
	insert := u.userRepo.AddUser(username, password, remark)
	if insert <= 0 {
		return common.NewCustomError(500, "添加用户失败")
	}
	return nil
}

func (u *UserServiceImpl) ListUser(username string, page, size int) (*models.Page, error) {
	return u.userRepo.PageUser(username, page, size)
}
