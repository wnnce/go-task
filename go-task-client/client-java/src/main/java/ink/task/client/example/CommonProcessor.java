package ink.task.client.example;

import ink.task.core.ClusterProcessor;
import ink.task.core.GoTask;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-27 16:36:45
 * @Description:
 */
public class CommonProcessor implements ClusterProcessor {
    @Override
    @GoTask("commonProcessor")
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("开始执行广播任务");
        logger.info("任务分片参数：{}", context.sharding());
        Thread.sleep(5);
        logger.info("任务执行完毕");
        return new TaskResult(true, "执行完成");
    }
}
