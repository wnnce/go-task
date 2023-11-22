package ink.task.processor;

import ink.task.core.ClusterProcessor;
import ink.task.core.GoTask;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 18:23:18
 * @Description:
 */
public class TestProcessor implements ClusterProcessor {
    @Override
    @GoTask("test")
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("任务开始执行");
        Thread.sleep(5000);
        logger.info("任务结束");
        return new TaskResult(true, "success");
    }
}
