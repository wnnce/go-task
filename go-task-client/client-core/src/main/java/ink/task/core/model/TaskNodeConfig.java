package ink.task.core.model;

import lombok.*;

/**
 * @Author: lisang
 * @DateTime: 2023-11-06 12:50:03
 * @Description: 任务节点配置类
 */
@Getter
@ToString
public class TaskNodeConfig {
    /**
     * 服务端地址
     */
    private String address;
    /**
     * 监听端口
     */
    private int port;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 心跳/信息上报时间间隔 单位：毫秒
     */
    private int intervals;
    /**
     * 连接服务器的超时时间
     */
    private int timeout;
    /**
     * 主线程组是否使用单线程
     */
    private boolean singleMain;

    private TaskNodeConfig() {}

    private TaskNodeConfig(Builder builder) {
        this.address = builder.address;
        this.port = builder.port;
        this.intervals = builder.intervals;
        this.timeout = builder.timeout;
        this.nodeName = builder.nodeName;
        this.singleMain = builder.singleMain;
    }
    public static class Builder {
        private String address;
        private int port;
        private String nodeName;
        private int intervals;
        private int timeout;
        private boolean singleMain;
        public Builder address(String address) {
            this.address = address;
            return this;
        }
        public Builder port(int port) {
            this.port = port;
            return this;
        }
        public Builder nodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }
        public Builder intervals(int intervals) {
            this.intervals = intervals;
            return this;
        }
        public Builder timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }
        public Builder singleMain(boolean singleMain) {
            this.singleMain = singleMain;
            return this;
        }
        public TaskNodeConfig build() {
            if (port <= 0) {
                port = 27221;
            }
            if (intervals < 5) {
                intervals = 5;
            }
            if (timeout <= 0) {
                timeout = 5000;
            }
            return new TaskNodeConfig(this);
        }
    }
}