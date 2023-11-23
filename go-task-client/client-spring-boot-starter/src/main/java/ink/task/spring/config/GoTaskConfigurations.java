package ink.task.spring.config;

import ink.task.core.*;
import ink.task.core.handler.AnnotationProcessorHandler;
import ink.task.core.handler.HttpServerHandler;
import ink.task.core.handler.NameProcessorHandler;
import ink.task.core.handler.WebSocketClientHandler;
import ink.task.core.model.TaskNodeConfig;
import ink.task.core.system.ControlService;
import ink.task.core.system.HandControlService;
import ink.task.core.util.GoTaskClient;
import ink.task.spring.BootstrapRunner;
import ink.task.spring.handler.BeanProcessorHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.net.http.HttpClient;
import java.util.Map;

/**
 * @Author: lisang
 * @DateTime: 2023-11-22 21:38:52
 * @Description: Go-Task 配置类
 */
public class GoTaskConfigurations {
    static class GoTaskNodeConfiguration {
        @Bean
        TaskNodeConfig taskNodeConfig(GoTaskProperties properties) {
            Assert.notNull(properties.nodeName(), "节点名称不能为空");
            Assert.notNull(properties.address(), "服务端地址不能为空");
            return new TaskNodeConfig.Builder()
                    .address(properties.address())
                    .nodeName(properties.nodeName())
                    .intervals(properties.intervals())
                    .port(properties.port())
                    .singleMain(properties.singleMain())
                    .timeout(properties.timeout())
                    .build();
        }

        @Bean
        BootstrapRunner bootstrapRunner(TaskNodeConfig config, HttpServerHandler serverHandler, WebSocketClientHandler clientHandler) {
            return new BootstrapRunner(config, serverHandler, clientHandler);
        }
    }
    static class SocketHandlerConfiguration {
        @Bean
        HttpServerHandler serverHandler(TaskProcessorSelector selector) {
            return new HttpServerHandler(selector);
        }
        @Bean
        WebSocketClientHandler clientHandler(ControlService controlService, TaskNodeConfig config) throws Exception {
            return new WebSocketClientHandler(controlService, config);
        }
    }
    static class SelectorConfiguration {
        @Bean
        TaskProcessorSelector processorSelector(TaskRunnerManager manager, AbstractTaskProcessorHandler taskHandler) {
            return new TaskProcessorSelector(manager, taskHandler);
        }
    }
    static class ManagerConfiguration {
        @Bean
        TaskRunnerManager runnerManager(TaskNodeConfig config, GoTaskClient client) {
            return new TaskRunnerManager(config, client);
        }
        @Bean
        ProcessorManager processorManager(ApplicationContext context) {
            final Map<String, SingleProcessor> singleProcessorMap = context.getBeansOfType(SingleProcessor.class);
            final Map<String, ClusterProcessor> clusterProcessorMap = context.getBeansOfType(ClusterProcessor.class);
            return new ProcessorManager(singleProcessorMap.values(), clusterProcessorMap.values());
        }
    }
    static class TaskProcessorHandlerConfiguration {
        @Bean
        AbstractTaskProcessorHandler processorHandler(ProcessorManager manager, ApplicationContext context) {
            AbstractTaskProcessorHandler nameHandler = new NameProcessorHandler(manager);
            AbstractTaskProcessorHandler annotationHandler = new AnnotationProcessorHandler(manager);
            nameHandler.setNextHandler(annotationHandler);
            AbstractTaskProcessorHandler beanHandler = new BeanProcessorHandler(manager, context);
            annotationHandler.setNextHandler(beanHandler);
            return nameHandler;
        }
    }

    static class OtherConfiguration {
        @Bean
        HttpClient httpClient() {
            return HttpClient.newHttpClient();
        }
        @Bean
        GoTaskClient goTaskClient(HttpClient client, TaskNodeConfig config) {
            return new GoTaskClient(client, config);
        }
        @Bean
        ControlService controlService() {
            return new HandControlService();
        }
    }
}
