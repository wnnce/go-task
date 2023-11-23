package ink.task.core.handler;

import ink.task.core.*;
import ink.task.core.enums.HandlerType;
import ink.task.core.exception.NotHandlerTypeException;
import ink.task.core.model.TaskInfo;

import java.util.Collection;
import java.util.List;

/**
 * @Author: lisang
 * @DateTime: 2023-11-17 20:43:28
 * @Description: 通过实现类方法返回的名称选择任务处理类
 */
public class NameProcessorHandler extends AbstractTaskProcessorHandler {
    public NameProcessorHandler(ProcessorManager manager) {
        super(manager);
    }

    @Override
    public <T extends Processor> T handler(TaskInfo taskInfo) throws Exception {
        final Integer handlerType = taskInfo.getHandlerType();
        if (handlerType.equals(HandlerType.FUNC_NAME.getType())) {
            Collection<? extends Processor> processorList = null;
            if (taskInfo.getTaskType() == 0) {
                processorList = manager.getSingleProcessorList();
            } else {
                processorList = manager.getClusterProcessorList();
            }
            if (processorList == null || processorList.isEmpty()) {
                return null;
            }
            for (Processor processor : processorList) {
                String name = null;
                if (processor instanceof SingleProcessor) {
                    name = ((SingleProcessor) processor).singleProcessorName();
                } else if (processor instanceof ClusterProcessor) {
                    name = ((ClusterProcessor) processor).clusterProcessorName();
                } else {
                    return null;
                }
                if (name.equals(taskInfo.getHandlerName())) {
                    return (T) processor;
                }
            }
            return null;
        } else if (nextHandler != null) {
            return nextHandler.handler(taskInfo);
        } else {
            throw new NotHandlerTypeException("不支持的处理器类型", taskInfo.getHandlerType());
        }
    }
}
