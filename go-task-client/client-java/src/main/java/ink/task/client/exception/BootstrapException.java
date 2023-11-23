package ink.task.client.exception;

/**
 * @Author: lisang
 * @DateTime: 2023-11-23 08:56:14
 * @Description: 任务节点启动失败异常
 */
public class BootstrapException extends RuntimeException {
    public BootstrapException(String message) {
        super(message);
    }
}
