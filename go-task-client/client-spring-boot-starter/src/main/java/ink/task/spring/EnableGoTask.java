package ink.task.spring;

import ink.task.spring.config.GoTaskAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @Author: lisang
 * @DateTime: 2023-11-27 14:59:28
 * @Description: 开启GoTask任务平台功能注解
 */

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(GoTaskAutoConfiguration.class)
public @interface EnableGoTask {
}
