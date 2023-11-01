package utils

import (
	sha2562 "crypto/sha256"
	"encoding/hex"
)

var sha256 = sha2562.New()

// Sha256Digest 字符串进行sha256摘要计算
func Sha256Digest(text string) string {
	data := []byte(text)
	sha256.Write(data)
	digest := sha256.Sum(nil)
	return hex.EncodeToString(digest)
}
