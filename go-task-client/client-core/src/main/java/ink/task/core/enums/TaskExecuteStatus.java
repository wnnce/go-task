package ink.task.core.enums;

/**
 * @Author: lisang
 * @DateTime: 2023-11-17 20:09:01
 * @Description: 任务运行状态常量类，用于本地管理任务运行
 */
public enum TaskExecuteStatus {
    /**
     * 运行成功
     */
    SUCCESS(0),
    /**
     * 任务运行中
     */
    RUNNING(1),
    /**
     * 任务被中断运行
     */
    STOPPED(2),
    /**
     * 任务异常运行结束
     */
    ANOMALY(3);
    private final int value;
    TaskExecuteStatus(int status) {
        this.value = status;
    }
    public int getValue() {
        return value;
    }
}
