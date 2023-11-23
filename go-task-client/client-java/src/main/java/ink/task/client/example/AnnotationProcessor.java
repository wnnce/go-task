package ink.task.client.example;

import ink.task.core.ClusterProcessor;
import ink.task.core.GoTask;
import ink.task.core.model.GoTaskContext;
import ink.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 16:14:06
 * @Description: 广播处理器实现
 */
public class AnnotationProcessor implements ClusterProcessor {
    @Override
    // 通过注解设置任务处理器名称
    @GoTask("annotationProcessor")
    public TaskResult processor(GoTaskContext context) throws Exception {
        // 输出广播运行的分片参数
        System.out.println(context.sharding());
        return new TaskResult(true);
    }
}
