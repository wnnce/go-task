package utils

import (
	"github.com/dgrijalva/jwt-go"
	"log"
	"time"
)

type Claims struct {
	ID   uint   `json:"id,omitempty"`
	Name string `json:"name,omitempty"`
	*jwt.StandardClaims
}

const tokenExpireTime = time.Hour * 24

const tokenKey = "hello world"

func GenerateToken(id uint, name string) string {
	claims := &Claims{
		id, name, &jwt.StandardClaims{
			ExpiresAt: time.Now().Add(tokenExpireTime).Unix(),
			Issuer:    "go-task",
		},
	}
	token := jwt.NewWithClaims(jwt.SigningMethodHS256, claims)
	tokenString, err := token.SignedString([]byte(tokenKey))
	if err != nil {
		return ""
	}
	return tokenString
}

func ParseToken(token string) bool {
	parseToken, err := jwt.ParseWithClaims(token, &Claims{}, func(token *jwt.Token) (interface{}, error) {
		return []byte(tokenKey), nil
	})
	if err != nil {
		log.Printf("Token解析异常，错误信息：%v\n", err)
		return false
	}
	_, ok := parseToken.Claims.(*Claims)
	return ok
}
