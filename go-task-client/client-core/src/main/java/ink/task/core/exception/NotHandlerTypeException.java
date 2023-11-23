package ink.task.core.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 15:59:51
 * @Description: 任务处理器类型不被处理异常
 */
public class NotHandlerTypeException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(NotHandlerTypeException.class);
    public NotHandlerTypeException(String message) {
        super(message);
    }
    public NotHandlerTypeException(String message, Integer handlerType) {
        super(message);
        logger.error("不受支持的任务处理器类型：{}", handlerType);
    }
}
