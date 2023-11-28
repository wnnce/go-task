package ink.task.example.processor;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 12:35:53
 * @Description:
 */
public class FuncNameProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        // 获取日志对象
        Logger logger = context.logger();
        // 获取任务参数
        String params = context.params();
        logger.info("运行nameProcessor");
        Thread.sleep(5000);
        return new TaskResult(true, "success");
    }

    /**
     * 使用方法的返回值作为任务处理器名称
     * @return 返回任务处理器名称
     */
    @Override
    public String singleProcessorName() {
        return "nameProcessor";
    }
}
