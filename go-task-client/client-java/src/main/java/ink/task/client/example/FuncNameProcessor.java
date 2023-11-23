package ink.task.client.example;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 16:10:57
 * @Description: 单机处理器实现
 */
public class FuncNameProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        // 任务参数
        System.out.println(context.params());
        logger.info("这是一段日志");
        logger.warn("错误信息：{}", "error");
        return new TaskResult(true, "success");
    }

    /**
     * 通过方法返回值查找任务处理器
     * @return 返回任务处理器名称
     */
    @Override
    public String singleProcessorName() {
        return "funcName";
    }
}
