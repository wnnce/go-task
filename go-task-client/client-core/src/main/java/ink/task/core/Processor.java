package ink.task.core;

import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:04:56
 * @Description: 任务处理注册接口 方法返回处理器的名称
 */
public interface Processor {
    TaskResult processor(GoTaskContext context) throws Exception;
}