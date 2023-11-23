package ink.task.core.model;

import ink.task.core.logging.Logger;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 19:25:52
 * @Description: 任务上下文，在调度时会传递给任务处理方法
 */
public class GoTaskContext {

    /**
     * 任务参数
     */
    private final String params;
    /**
     * 任务处理器的分片参数 如果是单机任务则为0,广播任务则是所有任务处理器的索引 不会重复
     */
    private final int sharding;
    /**
     * 日志对象，可以输出日志和获取缓存的日志字符串
     */
    private final Logger logger;

    public GoTaskContext(Builder builder) {
        this.params = builder.params;
        this.sharding = builder.sharding;
        this.logger = builder.logger;
    }

    public static class Builder {
        private String params;
        private int sharding;
        private Logger logger;

        public Builder() {}

        public Builder params(String params) {
            this.params = params;
            return this;
        }
        public Builder sharding(int sharding) {
            this.sharding = sharding;
            return this;
        }
        public Builder logger(Logger logger) {
            this.logger = logger;
            return this;
        }
        public GoTaskContext build() {
            return new GoTaskContext(this);
        }
    }

    public String params() {
        return params;
    }
    public int sharding() {
        return sharding;
    }
    public Logger logger() {
        return logger;
    }
}
