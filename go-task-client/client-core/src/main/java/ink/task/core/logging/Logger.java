package ink.task.core.logging;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 19:59:55
 * @Description:
 */
public interface Logger {
    String getLogsValue();
    void debug(String text);
    void debug(String text, Object ...params);
    void info(String text);
    void info(String text, Object ...params);
    void warn(String text);
    void warn(String text, Object ...params);
    void error(String text);
    void error(String text, Object ...params);
    void trace(String text);
    void trace(String text, Object ...params);
}
