package ink.task.client.example;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-27 15:35:41
 * @Description:
 */
public class HotTaskProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("任务开始运行");
        Thread.sleep(5000);
        logger.info("任务运行结束");
        return new TaskResult(true, "执行完成");
    }

    @Override
    public String singleProcessorName() {
        return "hotProcessor";
    }
}
