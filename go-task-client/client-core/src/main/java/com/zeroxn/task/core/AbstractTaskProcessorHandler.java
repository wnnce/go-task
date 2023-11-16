package com.zeroxn.task.core;

import com.zeroxn.task.core.model.TaskInfo;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 19:23:50
 * @Description: 任务处理器责任链抽象类
 */
public abstract class AbstractTaskProcessorHandler {
    protected AbstractTaskProcessorHandler nextHandler;

    public void setNextHandler(AbstractTaskProcessorHandler nextHandler) {
        this.nextHandler = nextHandler;
    }
    abstract public void handler(TaskInfo taskInfo);
}