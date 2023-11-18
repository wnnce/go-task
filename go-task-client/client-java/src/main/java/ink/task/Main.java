package ink.task;

import ink.task.core.TaskProcessorSelector;
import ink.task.core.logging.AbstractLogger;
import ink.task.core.logging.Logger;
import ink.task.core.logging.LoggerFactory;
import ink.task.core.logging.LoggerLevel;
import ink.task.core.model.TaskInfo;
import ink.task.core.model.TaskNodeConfig;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 18:11:16
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws Exception {
        /*TaskNodeConfig config = new TaskNodeConfig.Builder()
                .nodeName("demo")
                .build();
        TaskInfo taskInfo = new TaskInfo(1, 1, 1, "test", null, 0, LocalDateTime.now());
        TaskProcessorSelector selector = new TaskProcessorSelector(config);
        selector.doSelect(taskInfo);*/
        Logger logger = LoggerFactory.getLogger(LoggerLevel.DEBUG, Main.class);
        logger.info("{},{}", 1, 2);
        System.out.println(logger.getLogsValue());
    }
}