package ink.task.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

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
     * 运行记录Id
     */
    private Integer recordId;
    /**
     * 任务的运行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "GMT+8")
    private Date runnerTime;
    /**
     * 任务的结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "GMT+8")
    private Date closingTime;
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
