package ink.task.client.factory;

import ink.task.client.util.ClassUtils;
import ink.task.core.ClusterProcessor;
import ink.task.core.ProcessorManager;
import ink.task.core.SingleProcessor;
import ink.task.core.TaskRunnerManager;
import ink.task.core.model.TaskNodeConfig;
import ink.task.core.util.GoTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 20:20:11
 * @Description: 管理类工厂，用户获取任务/处理器管理对象
 */
public final class ManagerFactory {
    private static final Logger logger = LoggerFactory.getLogger(ManagerFactory.class);
    private static final AtomicReference<ProcessorManager> processorManagerReference = new AtomicReference<>();
    private static final AtomicReference<TaskRunnerManager> runnerManagerReference = new AtomicReference<>();
    private ManagerFactory() {}

    /**
     * 实例化ProcessorManager 任务处理器管理类 通过原子类实现单例模式
     * @return 返回任务处理器管理类
     */
    public static ProcessorManager newProcessorManager() {
        ProcessorManager processorManager = processorManagerReference.get();
        if (processorManager == null) {
            final List<Class<? extends SingleProcessor>> interfaceImpl = ClassUtils.getInterfaceImpl(SingleProcessor.class);
            final List<SingleProcessor> singleProcessorList = new CopyOnWriteArrayList<>();
            for (final Class<? extends SingleProcessor> aClass : interfaceImpl) {
                try {
                    SingleProcessor singleProcessor = aClass.getDeclaredConstructor().newInstance();
                    singleProcessorList.add(singleProcessor);
                } catch (Exception ex) {
                    logger.error("实例化SingleProcessor实现类报错，错误信息：{}，Class：{}", ex.getMessage(), aClass.toString());
                }
            }
            final List<Class<? extends ClusterProcessor>> interfaceImpl1 = ClassUtils.getInterfaceImpl(ClusterProcessor.class);
            final List<ClusterProcessor> clusterProcessorList = new CopyOnWriteArrayList<>();
            for (final Class<? extends ClusterProcessor> aClass : interfaceImpl1) {
                try {
                    ClusterProcessor clusterProcessor = aClass.getDeclaredConstructor().newInstance();
                    clusterProcessorList.add(clusterProcessor);
                } catch (Exception ex) {
                    logger.error("实例化ClusterProcessor实现类报错，错误信息：{}，Class：{}", ex.getMessage(), aClass.toString());
                }
            }
            ProcessorManager newProcessorManager = new ProcessorManager(singleProcessorList, clusterProcessorList);
            if (processorManagerReference.compareAndSet(null, newProcessorManager)) {
                processorManager = newProcessorManager;
            } else {
                processorManager = processorManagerReference.get();
            }
        }
        return processorManager;
    }

    /**
     * 实例化RunnerManager 任务运行管理器 通过原子类实现单例模式
     * @return 返回TaskRunnerManager任务运行管理器
     */
    public static TaskRunnerManager newRunnerManager(TaskNodeConfig config) {
        TaskRunnerManager runnerManager = runnerManagerReference.get();
        if (runnerManager == null) {
            final GoTaskClient client = new GoTaskClient(HttpClient.newHttpClient(), config);
            TaskRunnerManager newRunnerManager = new TaskRunnerManager(config, client);
            if (runnerManagerReference.compareAndSet(null, newRunnerManager)) {
                runnerManager = newRunnerManager;
            } else {
                runnerManager = runnerManagerReference.get();
            }
        }
        return runnerManager;
    }
}
