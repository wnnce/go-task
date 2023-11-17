package ink.task.core.model;

import lombok.Getter;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 19:25:52
 * @Description: 任务上下文，在调度时会传递给任务处理方法
 */
@Getter
public class GoTaskContext {

    private final String params;

    private final int sharding;

    public GoTaskContext(Builder builder) {
        this.params = builder.params;
        this.sharding = builder.sharding;
    }

    public static class Builder {
        private String params;
        private int sharding;

        public Builder() {}

        public Builder params(String params) {
            this.params = params;
            return this;
        }
        public Builder sharding(int sharding) {
            this.sharding = sharding;
            return this;
        }
        public GoTaskContext build() {
            return new GoTaskContext(this);
        }
    }
}
