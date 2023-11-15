package com.zeroxn.task.core;

import com.zeroxn.task.core.model.GoTaskContext;
import com.zeroxn.task.core.model.Result;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:08:11
 * @Description: 单机任务处理器接口
 */
public interface SingleProcessor extends ProcessorRegister {
    Result processor(GoTaskContext context) throws Exception;
    @Override
    default String processorName() {
        return "";
    }
}
