package ink.task.example.processor;

import ink.task.core.GoTask;
import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import ink.task.core.util.JsonUtils;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 12:39:01
 * @Description:
 */
public class AnnotationProcessor implements SingleProcessor {

    @Override
    // 使用注解声明处理器名称
    @GoTask("annotationProcessor")
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("运行annotationProcessor");
        String value = context.params();
        TaskParams params = JsonUtils.form(value, TaskParams.class);
        if (params == null) {
            throw new NullPointerException("任务运行参数不能为空");
        }
        Thread.sleep(5000);
        logger.info("运行参数：{}", params);
        return new TaskResult(true, "success");
    }
    record TaskParams(Integer id, String name) {}
}
