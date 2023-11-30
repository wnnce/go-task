package config

import (
	"github.com/gofiber/fiber/v2/log"
	"gopkg.in/yaml.v2"
	"os"
	"path/filepath"
)

type Config struct {
	Server   *ServerEntity `yaml:"server"`
	Database *DbEntity     `yaml:"database"`
}

type ServerEntity struct {
	Port int `yaml:"port"` // 服务器端口
}

type DbEntity struct {
	Host     string `yaml:"host"`     // 数据库地址
	Port     int    `yaml:"port"`     // 数据库端口
	Username string `yaml:"username"` // 数据库用户名
	Password string `yaml:"password"` // 数据库密码
	Dbname   string `yaml:"dbname"`   // 数据库名称
}

func GetConfig() *Config {
	dir, err := os.Getwd()
	if err != nil {
		log.Errorf("获取工作目录失败，错误信息：%v\n", err)
		return nil
	}
	path := filepath.Join(dir, "internal/config/config.yaml")
	file, err := os.ReadFile(path)
	if err != nil {
		log.Errorf("读取配置文件失败，错误信息：%v\n", err)
		return nil
	}
	conf := &Config{}
	err = yaml.Unmarshal(file, conf)
	if err != nil {
		log.Errorf("解析配置文件错误，错误信息：%v\n", err)
		return nil
	}
	return conf
}
