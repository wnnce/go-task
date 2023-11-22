package ink.task.core.model;

import ink.task.core.enums.TaskExecuteStatus;
import ink.task.core.logging.Logger;
import lombok.ToString;

import java.util.concurrent.Future;

/**
 * @Author: lisang
 * @DateTime: 2023-11-17 20:17:49
 * @Description: 任务运行在管理类中的缓存对象
 */
@ToString
public class TaskCache {
    /**
     * 运行任务的异步任务
     */
    private final Future<TaskResult> future;
    /**
     * 任务在本地的运行状态 常量类
     */
    private TaskExecuteStatus status;
    /**
     * 任务返回给服务端的结果
     */
    private final TaskExecuteResult result;

    /**
     * 任务运行时传递的日志接口类，可以获取日志信息
     */
    private final Logger logger;
    public TaskCache(Future<TaskResult> future, TaskExecuteStatus status, TaskExecuteResult result, Logger logger) {
        this.future = future;
        this.status = status;
        this.result = result;
        this.logger = logger;

    }
    public Future<TaskResult> future() {
        return future;
    }

    public TaskExecuteStatus status() {
        return status;
    }
    public TaskExecuteResult result() {
        return result;
    }
    public Logger logger() {
        return logger;
    }

    public void setStatus(TaskExecuteStatus status) {
        this.status = status;
    }
}
