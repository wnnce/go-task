package com.zeroxn.task.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: lisang
 * @DateTime: 2023-11-15 19:57:25
 * @Description: 任务处理器方法注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GoTask {
    String value() default "";
}