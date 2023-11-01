package user

import (
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"go-task-server/internal/utils"
	"log"
)

type UserService interface {
	Login(username, password, ip string) (string, error)
	AddUser(username, password, remark string) error
	ListUser(username string, page, size int) (*models.Page, error)
	DeleteUser(userId int) error
}

type UserServiceImpl struct {
	userRepo UserRepository
}

func NewUserService(userRepo UserRepository) UserService {
	return &UserServiceImpl{userRepo: userRepo}
}

func (u *UserServiceImpl) Login(username, password, ip string) (string, error) {
	log.Printf("传入的原始密码：%v\n", password)
	password = utils.Sha256Digest(password)
	log.Printf("Sha256摘要后的密码：%v\n", password)
	findUser, err := u.userRepo.Login(username, password)
	if err != nil {
		return "", common.NewCustomError(500, "登录失败")
	}
	if findUser == nil {
		return "", common.NewCustomError(400, "用户名或密码错误")
	}
	log.Printf("用户{%v}登陆成功", username)
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
	password = utils.Sha256Digest(password)
	insert := u.userRepo.AddUser(username, password, remark)
	if insert <= 0 {
		return common.NewCustomError(500, "添加用户失败")
	}
	return nil
}

func (u *UserServiceImpl) ListUser(username string, page, size int) (*models.Page, error) {
	return u.userRepo.PageUser(username, page, size)
}

func (u *UserServiceImpl) DeleteUser(userId int) error {
	result := u.userRepo.DeleteUserById(userId)
	if result <= 0 {
		return common.NewCustomError(404, "删除的用户不存在")
	}
	return nil
}
