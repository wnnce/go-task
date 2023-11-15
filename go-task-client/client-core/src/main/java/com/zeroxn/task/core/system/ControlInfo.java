package com.zeroxn.task.core.system;

import lombok.*;

/**
 * @Author: lisang
 * @DateTime: 2023-11-06 10:34:34
 * @Description: 系统监控信息实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class ControlInfo {
    /**
     * CPU使用率
     */
    private double usedCpu;
    /**
     * 总物理内存
     */
    private long totalMemory;
    /**
     * 已使用物理内存
     */
    private long usedMemory;
    /**
     * 总磁盘容量
     */
    private long totalDisk;
    /**
     * 已使用磁盘容量
     */
    private long usedDisk;
    /**
     * IP地址
     */
    private String ipAddress;
}
