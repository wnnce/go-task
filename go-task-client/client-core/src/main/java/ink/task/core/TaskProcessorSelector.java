package ink.task.core;

import ink.task.core.exception.NotHandlerTypeException;
import ink.task.core.model.TaskExecuteResult;
import ink.task.core.model.TaskInfo;
import ink.task.core.util.GoTaskClient;
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
    private final GoTaskClient client;
    public TaskProcessorSelector(TaskRunnerManager manager, AbstractTaskProcessorHandler taskHandler, GoTaskClient client) {
        this.taskHandler = taskHandler;
        this.manager = manager;
        this.client = client;
    }
    public void doSelect(TaskInfo taskInfo) {
        final Integer taskType = taskInfo.getTaskType();
        if (taskType < 0 || taskType > 1) {
            handleUnsupportedTask(taskType, taskInfo.getTaskId(), taskInfo.getRecordId());
            return;
        }
        Future<?> future = executors.submit(() -> {
            try {
                Processor handler = taskHandler.handler(taskInfo);
                logger.debug("任务处理器获取成功：{}，开始执行任务", handler.toString());
                manager.execute(handler, taskInfo);
            } catch (NotHandlerTypeException ex) {
                handleNotHandlerTypeException(taskInfo.getHandlerType(), taskInfo.getTaskId(), taskInfo.getRecordId());
            } catch (Exception ex) {
                handleSelectedException(taskInfo.getTaskId(), taskInfo.getRecordId(), ex);
            }
        });
    }

    private void handleUnsupportedTask(final int taskType, final int taskId, final int recordId) {
        logger.error("不支持的任务类型，taskType：{}", taskType);
        client.sendException(taskId, recordId, "当前任务节点不支持此任务类型");
    }
    private void handleNotHandlerTypeException(final int handlerType, final int taskId, final int recordId) {
        logger.error("不支持的任务处理器类型，handlerType：{}", handlerType);
        client.sendException(taskId, recordId, "不支持的任务处理器类型");
    }
    private void handleSelectedException(final int taskId, final int recordId, final Exception ex) {
        final String message = ex.getMessage();
        logger.error("任务处理器选择异常，错误信息：{}", message);
        client.sendException(taskId, recordId, "获取任务处理器异常，错误信息：" + message);
    }
    public void shutdownExecutor() {
        executors.shutdown();
    }
}
