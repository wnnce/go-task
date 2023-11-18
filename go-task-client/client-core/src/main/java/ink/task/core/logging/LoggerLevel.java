package ink.task.core.logging;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 20:03:08
 * @Description: 日志级别常量类
 */
public enum LoggerLevel {
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    TRACE(5);
    final int value;
    LoggerLevel(int level) {
        this.value = level;
    }
}
