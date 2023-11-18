package ink.task.core.logging;

import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-18 20:09:11
 * @Description: 日志实现抽象类
 */
public abstract class AbstractLogger implements Logger{
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger("TaskLogger");
    protected String name;
    protected Class<?> clazz;
    protected final LoggerLevel level;
    private final StringBuilder logsCache = new StringBuilder();
    public AbstractLogger(String name, LoggerLevel level) {
        this.name = name;
        this.level = level;
    }
    public AbstractLogger(Class<?> clazz, LoggerLevel level) {
        this.clazz = clazz;
        this.level = level;
    }

    @Override
    public String getLogsValue() {
        return logsCache.toString();
    }

    @Override
    public void debug(String text) {
        logger.debug(text);
        final String log = this.formatLog(LoggerLevel.DEBUG, text);
        if (LoggerLevel.DEBUG.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void debug(String text, Object... params) {
        logger.debug(text, params);
        final String log = this.formatLog(LoggerLevel.DEBUG, text, params);
        if (LoggerLevel.DEBUG.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void info(String text) {
        logger.info(text);
        final String log = this.formatLog(LoggerLevel.INFO, text);
        if (LoggerLevel.INFO.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void info(String text, Object... params) {
        logger.info(text, params);
        final String log = this.formatLog(LoggerLevel.INFO, text, params);
        if (LoggerLevel.INFO.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void warn(String text) {
        logger.warn(text);
        final String log = this.formatLog(LoggerLevel.WARN, text);
        if (LoggerLevel.WARN.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void warn(String text, Object... params) {
        logger.warn(text, params);
        final String log = this.formatLog(LoggerLevel.WARN, text, params);
        if (LoggerLevel.WARN.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void error(String text) {
        logger.error(text);
        final String log = this.formatLog(LoggerLevel.ERROR, text);
        if (LoggerLevel.ERROR.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void error(String text, Object... params) {
        logger.error(text, params);
        final String log = this.formatLog(LoggerLevel.ERROR, text, params);
        if (LoggerLevel.ERROR.value >= level.value) {
            logsCache.append(log);
        }
    }

    @Override
    public void trace(String text) {
        logger.trace(text);
        final String log = this.formatLog(LoggerLevel.TRACE, text);
        if (level.value >= 0) {
            logsCache.append(log);
        }
    }

    @Override
    public void trace(String text, Object... params) {
        logger.trace(text, params);
        final String log = this.formatLog(LoggerLevel.TRACE, text, params);
        if (level.value >= 0) {
            logsCache.append(log);
        }
    }
    protected abstract String formatLog(LoggerLevel level, String text, Object[] args);
    protected abstract String formatLog(LoggerLevel level, String text);
}
