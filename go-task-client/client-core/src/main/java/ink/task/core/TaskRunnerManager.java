package ink.task.core;

import ink.task.core.enums.TaskExecuteStatus;
import ink.task.core.logging.LoggerLevel;
import ink.task.core.model.*;
import ink.task.core.util.GoTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 22:53:42
 * @Description:
 */
public final class TaskRunnerManager {
    private static final Logger logger = LoggerFactory.getLogger(TaskRunnerManager.class);
    /**
     * 执行任务的线程池，默认线程为当前CPU核数的两倍
     */
    private static final ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    private final Map<Integer, TaskCache> taskMap = new ConcurrentHashMap<>();
    private GoTaskClient client;
    private TaskNodeConfig config;
    public TaskRunnerManager(TaskNodeConfig config, GoTaskClient client) {
        this.config = config;
        this.client = client;
    }
    private TaskRunnerManager() {}
    public <T extends Processor> void execute(T processor, TaskInfo taskInfo) {
        // 任务处理器为空的处理逻辑
        if (processor == null) {
            final TaskExecuteResult result = TaskExecuteResult.builder()
                    .taskId(taskInfo.getTaskId())
                    .recordId(taskInfo.getRecordId())
                    .status(1)
                    .outcome("找不到任务处理器")
                    .nodeName(config.getNodeName())
                    .build();
            client.send(result);
            return;
        }
        final Integer taskId = taskInfo.getTaskId();
        final ink.task.core.logging.Logger taskLogger = ink.task.core.logging.LoggerFactory.getLogger(LoggerLevel.INFO, processor.getClass());
        Future<TaskResult> future = executors.submit(() -> {
            final GoTaskContext context = new GoTaskContext.Builder()
                    .params(taskInfo.getParams())
                    .sharding(taskInfo.getSharding())
                    .logger(taskLogger)
                    .build();
            String errMessage = "";
            for (int i = 0; i < 3; i++) {
                if (i > 0) {
                    // 任务重试时清空日志上一次执行保存的日志信息
                    taskLogger.clearLogsValue();
                }
                try {
                    return processor.processor(context);
                } catch (Exception ex) {
                    errMessage = ex.getMessage();
                    logger.warn("处理器方法执行异常，处理器：{}，错误信息：{}", processor.toString(), ex.getMessage());
                }
            }
            return new TaskResult(false, "处理器方法执行异常，错误信息：" + errMessage);
        });
        final TaskExecuteResult executeResult = TaskExecuteResult.builder()
                .taskId(taskId)
                .recordId(taskInfo.getRecordId())
                .nodeName(config.getNodeName())
                .runnerTime(new Date()).build();
        final TaskCache task = new TaskCache(future, TaskExecuteStatus.RUNNING, executeResult, taskLogger);
        taskMap.put(taskId, task);
        this.await(taskId, task);
    }

    private void await(Integer taskId, TaskCache task) {
        executors.submit(() -> {
            logger.info(this.find(taskId).toString());
            final Future<TaskResult> future = task.future();
            final TaskExecuteResult executeResult = task.result();
            try {
                final TaskResult result = future.get();
                task.setStatus(TaskExecuteStatus.SUCCESS);
                executeResult.setStatus(result.getResult() ? 0 : 1);
                executeResult.setClosingTime(new Date());
                executeResult.setOutcome(result.getMessage());
                executeResult.setRunnerLogs(task.logger().getLogsValue());
            } catch (Exception ex) {
                logger.info("等待任务执行异常，错误信息：{}", ex.getMessage());
                task.setStatus(TaskExecuteStatus.ANOMALY);
                executeResult.setClosingTime(new Date());
                if (!future.isDone() || !future.isCancelled()) {
                    future.cancel(true);
                }
                executeResult.setClosingTime(new Date());
                executeResult.setStatus(1);
                executeResult.setOutcome("方法执行异常，错误信息：" + ex.getMessage());
            }
            client.send(executeResult);
            delete(taskId);
        }, executors);
    }
    public TaskCache find(Integer taskId) {
        return taskMap.get(taskId);
    }
    public void close(Integer taskId) {
        final TaskCache task = taskMap.get(taskId);
        if (task == null) {
            return;
        }
        Future<TaskResult> future = task.future();
        if (future.isDone()) {
            logger.info("任务已经完成，无法取消");
        } else if (future.isCancelled()) {
            logger.info("任务已经取消，无法再次取消");
        } else {
            boolean result = future.cancel(true);
            if (result) {
                task.setStatus(TaskExecuteStatus.STOPPED);
            }
        }
    }

    public boolean delete(Integer taskId) {
        final TaskCache task = taskMap.get(taskId);
        if (task == null) {
            logger.info("任务不存在，跳过处理");
            return true;
        }
        final Future<TaskResult> future = task.future();
        if (future.isDone() || future.isCancelled()) {
            logger.info("任务运行完成，删除任务，taskId：{}", taskId);
            taskMap.remove(taskId);
            return true;
        }
        logger.warn("任务运行中，删除任务失败");
        return false;
    }

    public void shutdownExecutor() {
        executors.shutdown();
    }
}
