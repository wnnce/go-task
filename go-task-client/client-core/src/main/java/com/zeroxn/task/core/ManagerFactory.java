package com.zeroxn.task.core;

import com.zeroxn.task.core.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 20:20:11
 * @Description: 管理类工厂，用户获取任务/处理器管理对象
 */
public final class ManagerFactory {
    private static final Logger logger = LoggerFactory.getLogger(ManagerFactory.class);
    private static final AtomicReference<ProcessorManager> processorManagerReference = new AtomicReference<>();

    private ManagerFactory() {}
    public static ProcessorManager newProcessorManager() {
        ProcessorManager processorManager = processorManagerReference.get();
        if (processorManager == null) {
            final List<Class<? extends SingleProcessor>> interfaceImpl = ClassUtils.getInterfaceImpl(SingleProcessor.class);
            final List<SingleProcessor> singleProcessorList = new CopyOnWriteArrayList<>();
            for (final Class<? extends SingleProcessor> aClass : interfaceImpl) {
                try {
                    SingleProcessor singleProcessor = aClass.getDeclaredConstructor().newInstance();
                    singleProcessorList.add(singleProcessor);
                } catch (Exception ex) {
                    logger.error("实例化SingleProcessor实现类报错，错误信息：{}，Class：{}", ex.getMessage(), aClass.toString());
                }
            }
            final List<Class<? extends ClusterProcessor>> interfaceImpl1 = ClassUtils.getInterfaceImpl(ClusterProcessor.class);
            final List<ClusterProcessor> clusterProcessorList = new CopyOnWriteArrayList<>();
            for (final Class<? extends ClusterProcessor> aClass : interfaceImpl1) {
                try {
                    ClusterProcessor clusterProcessor = aClass.getDeclaredConstructor().newInstance();
                    clusterProcessorList.add(clusterProcessor);
                } catch (Exception ex) {
                    logger.error("实例化ClusterProcessor实现类报错，错误信息：{}，Class：{}", ex.getMessage(), aClass.toString());
                }
            }
            ProcessorManager newProcessorManager = new ProcessorManager(singleProcessorList, clusterProcessorList);
            if (processorManagerReference.compareAndSet(null, newProcessorManager)) {
                processorManager = newProcessorManager;
            } else {
                processorManager = processorManagerReference.get();
            }
        }
        return processorManager;
    }
}
