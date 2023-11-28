package ink.task.example.processor;

import ink.task.core.ClusterProcessor;
import ink.task.core.GoTask;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 12:49:49
 * @Description: 广播处理器实现
 */
public class CustomProcessor implements ClusterProcessor {
    @Override
    @GoTask("customProcessor")
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("任务执行参数：{}", context.params());
        logger.info("任务处理器分片参数：{}", context.sharding());
        Thread.sleep(3000);
        return new TaskResult(true, "success");
    }
}