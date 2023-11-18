package ink.task.processor;

import ink.task.core.SingleProcessor;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 18:19:20
 * @Description:
 */
public class DemoProcessor implements SingleProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DemoProcessor.class);
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        logger.debug("任务开始执行");
        Thread.sleep(5000);
        logger.debug("任务执行结束");
        return new TaskResult(true, "success");
    }

    @Override
    public String singleProcessorName() {
        return "demo";
    }
}
