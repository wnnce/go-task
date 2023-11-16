package com.zeroxn.task.core.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 18:09:26
 * @Description:
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TaskResult {
    private Boolean result;
    private String message;

    public TaskResult(Boolean result) {
        this.result = result;
    }
}
