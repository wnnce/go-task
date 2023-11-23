package ink.task.client.example;

import ink.task.client.TaskNodeBootstrap;
import ink.task.core.model.TaskNodeConfig;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 16:16:30
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        TaskNodeConfig config = new TaskNodeConfig.Builder()
                // 服务端地址
                .address("localhost:5400")
                // 当前节点名称
                .nodeName("demo")
                // 上报信息间隔
                .intervals(8)
                .build();
        TaskNodeBootstrap bootstrap = new TaskNodeBootstrap();
        bootstrap.start(config).sync();
    }
}
