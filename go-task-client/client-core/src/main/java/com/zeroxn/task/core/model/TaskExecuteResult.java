package com.zeroxn.task.core.model;

import lombok.*;

import java.time.LocalDateTime;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 18:23:47
 * @Description: 任务运行结果参数实体类，用于上报给调度中心
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class TaskExecuteResult {
    /**
     * 任务Id
     */
    private Integer taskId;
    /**
     * 任务的运行时间
     */
    private LocalDateTime runnerTime;
    /**
     * 任务的结束时间
     */
    private LocalDateTime closingTime;
    /**
     * 任务的运行结果 0:成功 1：失败
     */
    private Integer status;
    /**
     * 任务的返回结果 可选
     */
    private String outcome;
    /**
     * 任务的运行日志
     */
    private String runnerLogs;
    /**
     * 执行任务的节点名称
     */
    private String nodeName;
}
