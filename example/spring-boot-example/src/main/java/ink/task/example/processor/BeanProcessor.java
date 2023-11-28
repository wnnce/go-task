package ink.task.example.processor;

import ink.task.core.SingleProcessor;
import ink.task.core.logging.Logger;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;
import org.springframework.stereotype.Component;

/**
 * @Author: lisang
 * @DateTime: 2023-11-28 13:04:51
 * @Description: 如果使用Bean处理器的话，可以直接在@Compont注解中添加任务处理器的名称即可
 */
@Component("springProcessor")
public class BeanProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        Logger logger = context.logger();
        logger.info("运行 springProcessor");
        Thread.sleep(5000);
        return new TaskResult(true, "success");
    }
}
