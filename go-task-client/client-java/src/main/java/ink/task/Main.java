package ink.task;

import ink.task.core.TaskNodeBootstrap;
import ink.task.core.TaskProcessorSelector;
import ink.task.core.model.TaskInfo;
import ink.task.core.model.TaskNodeConfig;

import java.time.LocalDateTime;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 18:11:16
 * @Description:
 */
public class Main {
    public static void main(String[] args) throws Exception {
        TaskNodeConfig config = new TaskNodeConfig.Builder()
                .address("localhost:5400")
                .nodeName("demo")
                .intervals(10)
                .build();
        TaskNodeBootstrap bootstrap = new TaskNodeBootstrap();
        bootstrap.start(config);
    }
}