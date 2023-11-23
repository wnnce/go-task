package ink.task.spring.handler;

import ink.task.core.*;
import ink.task.core.enums.HandlerType;
import ink.task.core.exception.NotHandlerTypeException;
import ink.task.core.model.TaskInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 21:39:37
 * @Description: 通过Spring IOC容器选择任务处理器类
 */
public class BeanProcessorHandler extends AbstractTaskProcessorHandler {

    private final ApplicationContext context;
    public BeanProcessorHandler(ProcessorManager manager, ApplicationContext context) {
        super(manager);
        this.context = context;
    }

    @Override
    public <T extends Processor> T handler(TaskInfo taskInfo) throws Exception {
        final Integer handlerType = taskInfo.getHandlerType();
        if (handlerType.equals(HandlerType.SPRING_BEAN.getType())) {
            Processor processor = null;
            if (taskInfo.getTaskType() == 0) {
                processor = context.getBean(taskInfo.getHandlerName(), SingleProcessor.class);
            } else {
                processor = context.getBean(taskInfo.getHandlerName(), ClusterProcessor.class);
            }
            return (T) processor;
        } else if (nextHandler != null) {
            return nextHandler.handler(taskInfo);
        } else {
            throw new NotHandlerTypeException("不支持的处理器类型", handlerType);
        }
    }
}
