package ink.task.example.processor;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.springframework.stereotype.Component;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 13:02:20
 * @Description: Spring依赖下的任务处理器，必须将其注入到IOC容器中，因为默认使用IOC容器获取实现类
 */
@Component
public class NameProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("运行SpringBoot nameProcessor");
        Thread.sleep(5000);
        return new TaskResult(true, "success");
    }

    @Override
    public String singleProcessorName() {
        return "nameProcessor";
    }
}
