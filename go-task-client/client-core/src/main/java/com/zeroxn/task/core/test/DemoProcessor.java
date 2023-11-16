package com.zeroxn.task.core.test;

import com.zeroxn.task.core.SingleProcessor;
import com.zeroxn.task.core.model.GoTaskContext;
import com.zeroxn.task.core.model.TaskResult;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 21:05:58
 * @Description:
 */
public class DemoProcessor implements SingleProcessor {
    @Override
    public TaskResult processor(GoTaskContext context) throws Exception {
        System.out.println("demo");
        return null;
    }

    @Override
    public String singleProcessorName() {
        return "demo";
    }
}
