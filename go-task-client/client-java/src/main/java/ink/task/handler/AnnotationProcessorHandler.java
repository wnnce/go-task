package ink.task.handler;

import ink.task.core.*;
import ink.task.core.enums.HandlerType;
import ink.task.core.exception.NotHandlerTypeException;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskInfo;
import ink.task.core.util.GoTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 17:59:15
 * @Description: 通过方法注解选择任务任务类
 */
public class AnnotationProcessorHandler extends AbstractTaskProcessorHandler {
    private static final Logger logger = LoggerFactory.getLogger(AnnotationProcessorHandler.class);
    private final ProcessorManager manager = ManagerFactory.newProcessorManager();
    @Override
    public <T extends Processor> T handler(TaskInfo taskInfo) throws Exception {
        final Integer handlerType = taskInfo.getHandlerType();
        if (handlerType.equals(HandlerType.ANNOTATION.getType())) {
            List<? extends Processor> processorList = null;
            if (taskInfo.getHandlerType() == 0) {
                processorList = manager.getSingleProcessorList();
            } else {
                processorList = manager.getClusterProcessorList();
            }
            if (processorList == null || processorList.isEmpty()) {
                return null;
            }
            for (Processor processor : processorList) {
                Method method = null;
                try {
                    method = processor.getClass().getDeclaredMethod("processor", GoTaskContext.class);
                } catch (NoSuchMethodException ex) {
                    logger.error("获取任务处理器的\"processor\"方法异常，错误信息：{}", ex.getMessage());
                    ex.printStackTrace();
                }
                if (method != null) {
                    final GoTask annotation = method.getAnnotation(GoTask.class);
                    if (annotation != null && annotation.value().equals(taskInfo.getHandlerName())) {
                        return (T) processor;
                    }
                }
            }
            return null;
        } else if (nextHandler != null) {
            return nextHandler.handler(taskInfo);
        } else {
            throw new NotHandlerTypeException("不支持的处理器类型", handlerType);
        }
    }
}
