package com.zeroxn.task.core;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:08:11
 * @Description: 单机任务处理器接口
 */
public interface SingleProcessor extends Processor {
    default String singleProcessorName() {
        return "";
    }
}
