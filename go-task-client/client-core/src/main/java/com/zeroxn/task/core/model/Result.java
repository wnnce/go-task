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
public class Result {
    private Boolean result;
    private String message;

    public Result(Boolean result) {
        this.result = result;
    }
}
