package com.zeroxn.task.core;

import com.zeroxn.task.core.exception.NotHandlerTypeException;
import com.zeroxn.task.core.model.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 09:35:34
 * @Description: 任务处理选择器
 */
public class TaskProcessorSelector {
    private static final Logger logger = LoggerFactory.getLogger(TaskProcessorSelector.class);
    private static final ExecutorService executors = Executors.newFixedThreadPool(4);
    private final AbstractTaskProcessorHandler taskHandler;
    public TaskProcessorSelector() {
        this.taskHandler = TaskProcessHandlerFactory.newTaskHandler();
    }
    private void doSelect(TaskInfo taskInfo) {
        // 异步选择处理器
        Future<?> future = executors.submit(() -> {
            try {
                taskHandler.handler(taskInfo);
            } catch (NotHandlerTypeException ex) {
                logger.error("不支持的任务处理器类型，handlerType：{}", taskInfo.getHandlerType());
            }
        });
    }
}
