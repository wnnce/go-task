package ink.task.spring.example;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.springframework.stereotype.Component;

/**
 * @Author: lisang
 * @DateTime: 2023-11-23 14:09:49
 * @Description:
 */
@Component("beanProcessor")
public class BeanProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("runner");
        return new TaskResult(true, "success");
    }
}
