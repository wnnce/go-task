package ink.task.core.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 19:59:28
 * @Description:
 */
public class TaskLogger extends AbstractLogger {
    public TaskLogger(String name, LoggerLevel level) {
        super(name, level);
    }

    public TaskLogger(Class<?> clazz, LoggerLevel level) {
        super(clazz, level);
    }
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

    @Override
    protected String formatLog(LoggerLevel level, String text) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"));
        final String threadName = "[" + Thread.currentThread().getName() + "]";
        final String showName = this.name != null ? name : clazz.getCanonicalName();
        return now + "  " + threadName + "  " + level.toString() + "  " + showName + " --  " + text + "\n";
    }

}
