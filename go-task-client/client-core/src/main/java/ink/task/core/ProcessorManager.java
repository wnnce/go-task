package ink.task.core;


import lombok.Getter;

import java.util.List;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 19:53:28
 * @Description: 任务处理器管理类
 */
@Getter
public class ProcessorManager {
    private List<SingleProcessor> singleProcessorList;
    private List<ClusterProcessor> clusterProcessorList;
    private ProcessorManager() {}
    protected ProcessorManager(List<SingleProcessor> singleProcessorList, List<ClusterProcessor> clusterProcessorList) {
        this.singleProcessorList = singleProcessorList;
        this.clusterProcessorList = clusterProcessorList;
    }

    public void addSingleProcessor(SingleProcessor processor) {
        this.singleProcessorList.add(processor);
    }
    public void addClusterProcessor(ClusterProcessor processor) {
        this.clusterProcessorList.add(processor);
    }
    public void removeSingleProcessor(SingleProcessor processor) {
        this.singleProcessorList.remove(processor);
    }
    public void removeClusterProcessor(ClusterProcessor processor) {
        this.clusterProcessorList.remove(processor);
    }

}
