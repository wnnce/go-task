package ink.task.core.util;

import ink.task.core.model.TaskExecuteResult;
import ink.task.core.model.TaskNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: lisang
 * @DateTime: 2023-11-16 18:22:52
 * @Description: 上报任务执行结果的Client客户端类
 */
public class GoTaskClient {
    private static final Logger logger = LoggerFactory.getLogger(GoTaskClient.class);
    private static final String SERVER_URI = "/task/report";
    private final HttpClient client;
    private final TaskNodeConfig config;

    public GoTaskClient(HttpClient client, TaskNodeConfig config) {
        this.client = client;
        this.config = config;
    }

    /**
     * 发送任务执行异常消息
     * @param taskId 任务ID
     * @param recordId 运行记录ID
     * @param exceptionMessage 异常信息
     */
    public void sendException(Integer taskId, Integer recordId, String exceptionMessage) {
        final TaskExecuteResult result = TaskExecuteResult.builder()
                .taskId(taskId)
                .recordId(recordId)
                .status(1)
                .outcome(exceptionMessage)
                .nodeName(config.getNodeName())
                .build();
        this.send(result);
    }

    /**
     * 发送上报调度中心的统一请求
     * @param result 任务运行结果
     */
    public void send(TaskExecuteResult result) {
        if (result == null) {
            throw new RuntimeException("任务返回结果不能为空");
        }
        HttpRequest request = this.builderRequest(result);
        CompletableFuture<HttpResponse<String>> requestFuture = client.sendAsync(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        requestFuture.thenAccept(res -> {
            logger.info("发送任务结果成功，响应内容：{}", String.valueOf(res.body()));
        });
    }

    /**
     * 根据任务运行结果构建统一请求体
     * @param result 任务的执行结果
     * @return 返回构建的请求体
     */
    private HttpRequest builderRequest(TaskExecuteResult result) {
        String requestBody = JsonUtils.to(result);
        if (requestBody == null) {
            throw new RuntimeException("请求体序列化异常");
        }
        return HttpRequest.newBuilder()
                .uri(URI.create("http://" + config.getAddress() + SERVER_URI))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
    }
}