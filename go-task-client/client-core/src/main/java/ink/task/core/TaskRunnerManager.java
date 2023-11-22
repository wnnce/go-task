package ink.task.core;

import ink.task.core.enums.TaskExecuteStatus;
import ink.task.core.logging.LoggerLevel;
import ink.task.core.model.*;
import ink.task.core.util.GoTaskClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.*;

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
    TaskRunnerManager(TaskNodeConfig config) {
        this.config = config;
        this.client = new GoTaskClient(config);
    }
    private TaskRunnerManager() {}
    public <T extends Processor> void execute(T processor, TaskInfo taskInfo) {
        final Integer taskId = taskInfo.getId();
        final ink.task.core.logging.Logger taskLogger = ink.task.core.logging.LoggerFactory.getLogger(LoggerLevel.INFO, processor.getClass());
        Future<TaskResult> resultFuture = executors.submit(() -> {
            final GoTaskContext context = new GoTaskContext.Builder()
                    .params(taskInfo.getParams())
                    .sharding(taskInfo.getSharding())
                    .logger(taskLogger)
                    .build();
            for (int i = 0; i < 3; i++) {
                try {
                    return processor.processor(context);
                } catch (Exception ex) {
                    logger.warn("处理器方法执行异常，处理器：{}，错误信息：{}", processor.toString(), ex.getMessage());
                }
            }
            return new TaskResult(false, "处理器方法执行异常");
        });
        final TaskExecuteResult executeResult = TaskExecuteResult.builder()
                .taskId(taskId)
                .nodeName(config.getNodeName())
                .runnerTime(LocalDateTime.now()).build();
        final TaskCache task = new TaskCache(resultFuture, TaskExecuteStatus.RUNNING, executeResult, taskLogger);
        taskMap.put(taskId, task);
        this.await(taskId, task);
        logger.info("调用执行完毕，{}", System.currentTimeMillis());
    }

    private void await(Integer taskId, TaskCache task) {
        CompletableFuture.runAsync(() -> {
            logger.info(this.find(taskId).toString());
            final Future<TaskResult> future = task.future();
            final TaskExecuteResult executeResult = task.result();
            try {
                final TaskResult result = future.get();
                task.setStatus(TaskExecuteStatus.SUCCESS);
                executeResult.setStatus(result.getResult() ? 0 : 1);
                executeResult.setClosingTime(LocalDateTime.now());
                executeResult.setOutcome(result.getMessage());
                executeResult.setRunnerLogs(task.logger().getLogsValue());
                logger.info(result.toString());
            } catch (Exception ex) {
                logger.info("等待任务执行异常，错误信息：{}", ex.getMessage());
                task.setStatus(TaskExecuteStatus.ANOMALY);
                executeResult.setClosingTime(LocalDateTime.now());
                if (!future.isDone() || !future.isCancelled()) {
                    future.cancel(true);
                }
                executeResult.setClosingTime(LocalDateTime.now());
                executeResult.setStatus(1);
                executeResult.setOutcome("方法执行异常，错误信息：" + ex.getMessage());
            }
//            client.send(executeResult);
            System.out.println(task.logger().getLogsValue());
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
}
