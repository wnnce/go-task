package ink.task.client.factory;

import ink.task.core.AbstractTaskProcessorHandler;
import ink.task.client.util.ClassUtils;
import ink.task.core.ProcessorManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 15:21:23
 * @Description: 任务处理器责任链工厂类，通过反射自动获取实现了抽象类的处理器并构造为责任链。 通过原子类实现单例模式
 */
public final class TaskProcessHandlerFactory {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessHandlerFactory.class);
    private static final AtomicReference<AbstractTaskProcessorHandler> reference = new AtomicReference<>();

    public static AbstractTaskProcessorHandler newTaskHandler() {
        AbstractTaskProcessorHandler handler = reference.get();
        if (handler == null) {
            AbstractTaskProcessorHandler taskProcessorHandler = null;
            final List<Class<? extends AbstractTaskProcessorHandler>> implClassList = ClassUtils.getAbstractImpl(AbstractTaskProcessorHandler.class);
            final ProcessorManager manager = ManagerFactory.newProcessorManager();
            try {
                if (implClassList.size() == 1) {
                    final Class<? extends AbstractTaskProcessorHandler> aClass = implClassList.get(0);
                    taskProcessorHandler = aClass.getDeclaredConstructor(ProcessorManager.class).newInstance(manager);
                } else if (implClassList.size() > 1) {
                    AbstractTaskProcessorHandler oldHandler = null;
                    for (int i = implClassList.size() - 1; i >= 0; i--) {
                        final Class<? extends AbstractTaskProcessorHandler> aClass = implClassList.get(i);
                        final AbstractTaskProcessorHandler newHandler = aClass.getDeclaredConstructor(ProcessorManager.class).newInstance(manager);
                        if (oldHandler != null) {
                            newHandler.setNextHandler(oldHandler);
                        }
                        oldHandler = newHandler;
                    }
                    taskProcessorHandler = oldHandler;
                }
            } catch (ReflectiveOperationException ex) {
                logger.error("创建任务处理责任链异常，错误信息：{}", ex.getMessage());
            }
            if (reference.compareAndSet(null, taskProcessorHandler)) {
                handler = taskProcessorHandler;
            } else {
                handler = reference.get();
            }
        }
        return handler;
    }
}
