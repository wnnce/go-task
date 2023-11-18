package ink.task.core;

import ink.task.core.exception.NotHandlerTypeException;
import ink.task.core.model.TaskInfo;
import ink.task.core.model.TaskNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 09:35:34
 * @Description: 任务处理选择器，负责任务参数校验，选择任务的执行处理器
 */
public class TaskProcessorSelector {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorSelector.class);
    private static final ExecutorService executors = Executors.newFixedThreadPool(4);
    private final AbstractTaskProcessorHandler taskHandler;
    private final TaskRunnerManager manager;
    public TaskProcessorSelector(TaskNodeConfig config) {
        this.taskHandler = TaskProcessHandlerFactory.newTaskHandler();
        this.manager = ManagerFactory.newRunnerManager(config);
    }
    public void doSelect(TaskInfo taskInfo) {
        final Integer taskType = taskInfo.getTaskType();
        if (taskType < 0 || taskType > 1) {
            // TODO 任务类型不被支持的处理逻辑
            return;
        }
        Future<?> future = executors.submit(() -> {
            try {
                Processor handler = taskHandler.handler(taskInfo);
                logger.debug("任务处理器获取成功：{}，开始执行任务", handler.toString());
                manager.execute(handler, taskInfo);
            } catch (NotHandlerTypeException ex) {
                logger.error("不支持的任务处理器类型，handlerType：{}", taskInfo.getHandlerType());
            } catch (Exception ex) {
                logger.error("任务处理器选择异常，错误信息：{}", ex.getMessage());
            }
        });
    }
}
