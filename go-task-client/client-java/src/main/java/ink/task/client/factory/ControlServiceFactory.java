package ink.task.client.factory;

import ink.task.core.system.ControlService;
import ink.task.core.system.HandControlService;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: lisang
 * @DateTime: 2023-11-13 14:59:26
 * @Description: 工厂类，使用原子类确保返回单例
 */
public final class ControlServiceFactory{
    private static final AtomicReference<ControlService> reference = new AtomicReference<>();
    private ControlServiceFactory() {}

    public static synchronized ControlService newControlService() {
        ControlService controlService = reference.get();
        if (controlService == null) {
            ControlService newControlService = new HandControlService();
            if (reference.compareAndSet(null, newControlService)) {
                controlService = newControlService;
            } else {
                controlService = reference.get();
            }
        }
        return controlService;
    }
}
