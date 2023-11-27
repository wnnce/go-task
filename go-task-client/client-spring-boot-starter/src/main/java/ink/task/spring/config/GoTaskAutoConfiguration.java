package ink.task.spring.config;

import ink.task.core.Processor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import static ink.task.spring.config.GoTaskConfigurations.*;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 21:37:18
 * @Description: Go-Task 任务节点SprigBoot自动配置类
 */
@ConditionalOnClass(Processor.class)
@EnableConfigurationProperties(GoTaskProperties.class)
@Import({
        SocketHandlerConfiguration.class, SelectorConfiguration.class, ManagerConfiguration.class,
        TaskProcessorHandlerConfiguration.class, OtherConfiguration.class, GoTaskNodeConfiguration.class
})
public class GoTaskAutoConfiguration {
}
