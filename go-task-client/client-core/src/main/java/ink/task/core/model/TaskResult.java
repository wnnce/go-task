package ink.task.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:09:26
 * @Description: 任务处理器的返回结果类
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TaskResult {
    /**
     * 任务的执行结果 true：成功 false：失败
     */
    private Boolean result;
    /**
     * 需要返回的任务结果信息，可以为空
     */
    private String message;

    public TaskResult(Boolean result) {
        this.result = result;
    }
}
