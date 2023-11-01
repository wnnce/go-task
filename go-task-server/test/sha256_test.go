package test

import (
	"go-task-server/internal/utils"
	"testing"
)

func TestSha256(t *testing.T) {
	text := "hello world"
	digest := utils.Sha256Digest(text)
	println(digest)
}
