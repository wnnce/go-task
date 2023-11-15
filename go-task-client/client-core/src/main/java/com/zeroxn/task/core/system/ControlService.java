package com.zeroxn.task.core.system;

/**
 * @Author: lisang
 * @DateTime: 2023-11-06 10:32:10
 * @Description: 系统状态监控接口，获取系统实时信息
 */
public interface ControlService {
    /**
     * 获取全部系统信息
     * @return 返回所有信息
     */
    ControlInfo getAllInfo();

    /**
     * 获取系统CPU使用率
     * @return 返回CPU使用率
     */
    double getCpuUsed();

    /**
     * 获取系统总内存和已用内存
     * @return 数组 0：总内存 1：已用内存
     */
    long[] getMemoryUsed();

    /**
     * 获取系统总硬盘空间和已用硬盘空间
     * @return 数组 0：总空间  1：已用空间
     */
    long[] getDiskUsed();

    /**
     * 获取系统网络接口的IP地址
     * @return 返回IP地址或空字符串
     */
    String getHostIp();
}
