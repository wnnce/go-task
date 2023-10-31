package user

import (
	"go-task-server/internal/common"
	"go-task-server/internal/models"
	"log"
	"math"
	"xorm.io/xorm"
)

type UserRepository interface {
	Login(username, password string) *models.User
	AddUser(username, password, remark string) int64
	PageUser(name string, page, size int) (*models.Page, error)
	UpdateLastIpById(userId uint, ip string)
	ExistUserByName(name string) bool
}

type UserRepositoryImpl struct {
	engine *xorm.Engine
}

func NewUserRepository(engine *xorm.Engine) UserRepository {
	return &UserRepositoryImpl{engine: engine}
}

func (u *UserRepositoryImpl) Login(username, password string) *models.User {
	user := new(models.User)
	_, err := u.engine.Where("name = ? and password = ?", username, password).Get(user)
	if err != nil {
		log.Printf("用户登录查询异常，错误信息：%v\n", err)
	}
	return user
}

func (u *UserRepositoryImpl) AddUser(username, password, remark string) int64 {
	user := &models.User{
		Name:     username,
		Password: password,
		Remark:   remark,
	}
	insert, err := u.engine.Cols("name", "password", "remark").Insert(user)
	if err != nil {
		log.Printf("添加用户失败，错误信息：%v\n", err)
		return 0
	}
	return insert
}

func (u *UserRepositoryImpl) PageUser(name string, page, size int) (*models.Page, error) {
	session := u.engine.Cols("id")
	u.handlerSessionByName(session, name)
	total, _ := session.Count(&models.User{})
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
	listSession := u.engine.AllCols()
	u.handlerSessionByName(listSession, name)
	users := make([]models.User, 0)
	err := listSession.Limit(size, offset).Find(&users)
	if err != nil {
		log.Printf("查询用户列表失败，错误信息：%v\n", err)
		return nil, common.NewCustomError(500, "用户列表查询失败")
	}
	return &models.Page{
		Page:      page,
		Size:      size,
		Total:     total,
		TotalPage: maxPage,
		List:      &users,
	}, nil
}

func (u *UserRepositoryImpl) UpdateLastIpById(userId uint, lastIp string) {
	user := &models.User{
		LastLoginIp: lastIp,
	}
	_, err := u.engine.ID(userId).Update(user)
	if err != nil {
		log.Printf("更新用户登陆信息失败，错误信息：%v\n", err)
	}
}

func (u *UserRepositoryImpl) ExistUserByName(name string) bool {
	result, err := u.engine.Where("name = ?", name).Exist(&models.User{})
	if err != nil {
		log.Printf("查询用户是否存在失败，错误信息：%v\n", err)
		return true
	}
	return result
}

func (u *UserRepositoryImpl) handlerSessionByName(session *xorm.Session, name string) {
	if len(name) > 0 {
		session.Where("name like ?", "%"+name+"%")
	}
}
