package ink.task.client.example;

import ink.task.core.GoTask;
import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import ink.task.core.util.JsonUtils;

/**
 * @Author: lisang
 * @DateTime: 2023-11-27 13:12:58
 * @Description:
 */
public class ExceptionProcessor implements SingleProcessor {
    @Override
    @GoTask("exception")
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("接收到任务处理请求");
        String str = context.params();
        logger.info("反序列化任务参数");
        Params params = JsonUtils.form(str, Params.class);
        if (params == null) {
            throw new RuntimeException("任务运行时意外异常");
        }
        logger.info(params.toString());
        Thread.sleep(3000);
        return new TaskResult(true, "success");
    }
    record Params(Integer code, String name) {}
}
