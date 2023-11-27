package ink.task.core.logging;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 19:59:55
 * @Description: 顶级日志接口
 */
public interface Logger {
    /**
     * 获取本次日志对象保存的所有日志信息 （只保存创建时指定的日志等级（含以上）的日志信息）
     * @return 返回日志字符串
     */
    String getLogsValue();

    /**
     * 清空已经保存的所有日志信息
     */
    void clearLogsValue();
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
