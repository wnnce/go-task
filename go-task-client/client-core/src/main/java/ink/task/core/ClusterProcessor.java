package ink.task.core;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:13:02
 * @Description: 广播任务处理器接口
 */
public interface ClusterProcessor extends Processor {
    default String clusterProcessorName() {
        return "";
    }
}
