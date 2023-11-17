package ink.task.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 15:59:28
 * @Description:
 */
public class NotTaskTypeException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(NotTaskTypeException.class);
    public NotTaskTypeException(String message) {
        super(message);
    }
    public NotTaskTypeException(String message, Integer taskType) {
        super(message);
        logger.error("不受支持的任务类型：{}", taskType);
    }
}
