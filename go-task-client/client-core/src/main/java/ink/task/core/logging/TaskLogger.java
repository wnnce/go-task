package ink.task.core.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 19:59:28
 * @Description: 日志实现类，主要实现抽象类的两个日志格式化方法
 */
public class TaskLogger extends AbstractLogger {
    public TaskLogger(String name, LoggerLevel level) {
        super(name, level);
    }

    public TaskLogger(Class<?> clazz, LoggerLevel level) {
        super(clazz, level);
    }

    /**
     * 格式化日志，针对日志消息中的 {} 占位符进行格式化
     * 如果占位符对应的类型是数组类型，那么调用Arrays.toString()方法输出数组的实际值
     * 其他类型则调用 toString()方法
     * @param level 日志等级
     * @param text 包含占位符的日志消息
     * @param args 对应占位符的参数
     * @return 返回格式化后的日志字符串
     */
    @Override
    protected String formatLog(LoggerLevel level, String text, Object[] args) {
        int argIndex = 0;
        int prefixIndex = text.indexOf("{}");
        StringBuilder stringBuilder = new StringBuilder();
        while (prefixIndex != -1 && argIndex < args.length) {
            stringBuilder.append(text, 0, prefixIndex);
            Object object = args[argIndex++];
            // 判断是否为数组，默认数组输出内存地址
            if (object instanceof Object[] objects) {
                stringBuilder.append(Arrays.toString(objects));
            } else {
                stringBuilder.append(object.toString());
            }
            text = text.substring(prefixIndex + 2);
            prefixIndex = text.indexOf("{}");
        }
        stringBuilder.append(text);
        return this.formatLog(level, stringBuilder.toString());
    }

    /**
     * 返回统一格式的日志信息
     * @param level 日志等级
     * @param text 日志消息
     * @return 返回格式化后的日志字符串
     */
    @Override
    protected String formatLog(LoggerLevel level, String text) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"));
        final String threadName = "[" + Thread.currentThread().getName() + "]";
        final String showName = this.name != null ? name : clazz.getCanonicalName();
        return now + "  " + threadName + "  " + level.toString() + "  " + showName + " --  " + text + "\n";
    }

}
