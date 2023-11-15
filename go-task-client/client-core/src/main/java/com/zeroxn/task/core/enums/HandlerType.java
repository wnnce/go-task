package com.zeroxn.task.core.enums;

/**
 * @Author: lisang
 * @DateTime: 2023-11-06 12:38:31
 * @Description: 执行器类型枚举类
 */
public enum HandlerType {
    /**
     * SpringBean执行器
     */
    SPRING_BEAN(0),
    /**
     * 注解执行器
     */
    ANNOTATION(1);
    private final int type;
    HandlerType(int type) {
        this.type = type;
    }
    public int getType() {
        return this.type;
    }
}
