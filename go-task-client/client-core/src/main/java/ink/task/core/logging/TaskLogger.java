package ink.task.core.logging;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

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
        final String[] strings = text.split(Pattern.quote("{}"));
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            sb.append(strings[i]).append("{").append(i).append("}");
        }
        final String message = MessageFormat.format(sb.toString(), args);
        return this.formatLog(level, message);
    }

    @Override
    protected String formatLog(LoggerLevel level, String text) {
        final String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSS"));
        final String threadName = "[" + Thread.currentThread().getName() + "]";
        final String showName = this.name != null ? name : clazz.getCanonicalName();
        return now + "  " + threadName + "  " + level.toString() + "  " + showName + " --  " + text + "\n";
    }

}
