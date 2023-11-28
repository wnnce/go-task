package ink.task.example;

import ink.task.client.TaskNodeBootstrap;
import ink.task.core.model.TaskNodeConfig;

/**
 * @Author: lisang
 * @DateTime: 2023-11-27 20:08:07
 * @Description:
 */
public class NodeMain {
    public static void main(String[] args) {
        TaskNodeConfig config = new TaskNodeConfig.Builder()
                // 任务节点名称
                .nodeName("java-test")
                // 服务端地址
                .address("localhost:5400")
                // 信息上报时间间隔
                .intervals(10)
                .build();
        TaskNodeBootstrap bootstrap = new TaskNodeBootstrap();
        bootstrap.start(config);
    }
}
