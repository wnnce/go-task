package com.zeroxn.task.core.system;

/**
 * @Author: lisang
 * @DateTime: 2023-11-13 14:59:26
 * @Description: 工厂类，返回单例
 */
public final class ControlServiceFactory {
    private static volatile HandControlService controlService = null;
    private ControlServiceFactory() {}
    public static synchronized ControlService newControlService() {
        if (controlService == null) {
            controlService = new HandControlService();
        }
        return controlService;
    }
}
