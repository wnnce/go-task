package ink.task.client.factory;

import ink.task.core.AbstractTaskProcessorHandler;
import ink.task.core.ProcessorManager;
import ink.task.core.handler.AnnotationProcessorHandler;
import ink.task.core.handler.NameProcessorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            final ProcessorManager manager = ManagerFactory.newProcessorManager();
            NameProcessorHandler nameProcessorHandler = new NameProcessorHandler(manager);
            AnnotationProcessorHandler annotationProcessorHandler = new AnnotationProcessorHandler(manager);
            nameProcessorHandler.setNextHandler(annotationProcessorHandler);
            if (reference.compareAndSet(null, nameProcessorHandler)) {
                handler = nameProcessorHandler;
            } else {
                handler = reference.get();
            }
            // 通过循环遍历当前项目跟路径下的所有.class文件并通过反射获取抽象类所有的具体实现来构建责任器链，暂时弃用
            // 有待解决的两个问题，1、maven跨模块的项目下，只能获取当前模块的实现，不能获取所有模块中的实现类
            // 2、如果实现类中有参数依赖，通过反射构造的都是空参对象，无法注入参数

            /*AbstractTaskProcessorHandler taskProcessorHandler = null;
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
            }*/
        }
        return handler;
    }
}
