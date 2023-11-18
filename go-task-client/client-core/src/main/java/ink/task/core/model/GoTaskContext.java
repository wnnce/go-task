package ink.task.core.model;

import ink.task.core.logging.Logger;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 19:25:52
 * @Description: 任务上下文，在调度时会传递给任务处理方法
 */
public class GoTaskContext {

    private final String params;

    private final int sharding;
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
