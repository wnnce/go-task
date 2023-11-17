package ink.task.core;

import ink.task.core.model.TaskInfo;

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
    abstract public <T extends Processor> T handler(TaskInfo taskInfo) throws Exception;
}