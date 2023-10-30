package common

type CustomError struct {
	code    int
	message string
}

func NewCustomError(code int, message string) *CustomError {
	return &CustomError{code, message}
}

func (c *CustomError) Error() string {
	return c.message
}

func (c *CustomError) Code() int {
	return c.code
}

func (c *CustomError) Message() string {
	return c.message
}
