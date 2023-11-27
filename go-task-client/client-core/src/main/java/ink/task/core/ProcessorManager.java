package ink.task.core;


import lombok.Getter;

import java.util.Collection;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 19:53:28
 * @Description: 任务处理器管理类
 */
@Getter
public class ProcessorManager {
    private Collection<SingleProcessor> singleProcessorList;
    private Collection<ClusterProcessor> clusterProcessorList;
    private ProcessorManager() {}
    public ProcessorManager(Collection<SingleProcessor> singleProcessorList, Collection<ClusterProcessor> clusterProcessorList) {
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
