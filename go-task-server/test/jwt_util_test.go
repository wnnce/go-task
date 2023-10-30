package test

import (
	"go-task-server/internal/utils"
	"log"
	"testing"
)

func TestJwtTokenGenerate(t *testing.T) {
	token := utils.GenerateToken(1, "admin")
	log.Println(token)
	result := utils.ParseToken(token)
	log.Println(result)
	result2 := utils.ParseToken("asdadadoashdashdaskldjasdasda")
	println(result2)
}
