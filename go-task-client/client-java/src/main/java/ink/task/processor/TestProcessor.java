package ink.task.processor;

import ink.task.core.ClusterProcessor;
import ink.task.core.GoTask;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 18:23:18
 * @Description:
 */
public class TestProcessor implements ClusterProcessor {
    private static final Logger logger = LoggerFactory.getLogger(TestProcessor.class);
    @Override
    @GoTask("test")
    public TaskResult processor(GoTaskContext context) throws Exception {
        logger.debug("任务开始执行");
        Thread.sleep(5000);
        logger.debug("任务执行结束");
        return new TaskResult(false, "fail");
    }
}
