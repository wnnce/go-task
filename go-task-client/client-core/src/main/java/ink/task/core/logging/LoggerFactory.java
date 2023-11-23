package ink.task.core.logging;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 20:47:25
 * @Description: 日志接口工厂类
 */
public class LoggerFactory {
    public static Logger getLogger(LoggerLevel level, String name) {
        return new TaskLogger(name, level);
    }
    public static Logger getLogger(LoggerLevel level, Class<?> clazz) {
        return new TaskLogger(clazz, level);
    }
}
