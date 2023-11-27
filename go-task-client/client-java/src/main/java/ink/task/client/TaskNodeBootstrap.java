package ink.task.client;

import ink.task.client.factory.ControlServiceFactory;
import ink.task.client.factory.ManagerFactory;
import ink.task.client.factory.TaskProcessHandlerFactory;
import ink.task.core.AbstractTaskProcessorHandler;
import ink.task.core.TaskProcessorSelector;
import ink.task.core.TaskRunnerManager;
import ink.task.core.model.TaskNodeConfig;
import ink.task.core.handler.HttpServerHandler;
import ink.task.core.handler.WebSocketClientHandler;
import ink.task.core.system.ControlService;
import ink.task.core.util.GoTaskClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @Author: lisang
 * @DateTime: 2023-11-12 22:09:25
 * @Description: 启动主类，负责启动任务节点
 */
public final class TaskNodeBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(TaskNodeBootstrap.class);
    private static final String WS_URI = "/node/registration/ws/{id}?name=%s&intervals=%s&port=%s";
    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private NioEventLoopGroup clientGroup;
    private Channel serverChannel;
    private Channel clientChannel;

    /**
     * 任务节点启动方法，会启动Http服务器，WebSocket Client, 以及两条用于通知时间的Channel
     * @param config 任务节点配置
     * @return 返回启动器，可以调用sync()方法阻塞线程运行直到节点运行结束为止
     */
    public TaskNodeBootstrap start(TaskNodeConfig config) {
        if (config.getNodeName() == null || config.getNodeName().isEmpty()) {
            throw new NullPointerException("节点名称不能为空");
        }
        if (config.getAddress() == null || config.getAddress().isEmpty()) {
            throw new NullPointerException("服务端地址不能为空");
        }
        try {
            bootstrapWebSocketClient(config);
        } catch (Exception ex) {
            ex.printStackTrace();
            stop();
            throw new RuntimeException("启动失败，WebSocket连接失败");
        }
        try {
            bootstrapHttpServer(config);
        } catch (Exception ex) {
            ex.printStackTrace();
            stop();
            throw new RuntimeException("Http服务器启动失败");
        }
        return this;
    }

    /**
     * 阻塞线程直到任务节点所有连接全部断开，监听到关闭消息后会停止所有线程池运行
     */
    public void sync() {
        CompletableFuture<Void> clientFuture = CompletableFuture.runAsync(() -> {
            try {
                clientChannel.closeFuture().sync();
            } catch (Exception ex) {
                logger.error("等待WebSocket连接关闭失败，错误信息：{}", ex.getMessage());
            } finally {
                if (bossGroup != null && !bossGroup.isShutdown()) {
                    bossGroup.shutdownGracefully();
                }
                if (workerGroup != null && !workerGroup.isShutdown()) {
                    workerGroup.shutdownGracefully();
                }
            }
        });
        CompletableFuture<Void> serverFuture = CompletableFuture.runAsync(() -> {
            try {
                serverChannel.closeFuture().sync();
            } catch (Exception ex) {
                logger.error("等待Http服务器关闭失败，错误信息：{}", ex.getMessage());
            } finally {
                if (clientGroup != null && !clientGroup.isShutdown()) {
                    clientGroup.shutdownGracefully();
                }
            }
        });
        CompletableFuture.allOf(clientFuture, serverFuture).join();
    }
    // 停止服务
    public void stop() {
        if (serverChannel != null) {
            serverChannel.close();
        }
        if (clientChannel != null) {
            clientChannel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (clientGroup != null) {
            clientGroup.shutdownGracefully();
        }
    }

    /**
     * 启动Http服务器
     * @param config 任务节点配置
     * @throws Exception 顶级异常
     */
    private void bootstrapHttpServer(TaskNodeConfig config) throws Exception {
        bossGroup = config.isSingleMain() ? new NioEventLoopGroup(1) : new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        final TaskRunnerManager manager = ManagerFactory.newRunnerManager(config);
        final AbstractTaskProcessorHandler taskHandler = TaskProcessHandlerFactory.newTaskHandler();
        final TaskProcessorSelector selector = new TaskProcessorSelector(manager, taskHandler, new GoTaskClient(HttpClient.newHttpClient(), config));
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new HttpResponseEncoder())
                                .addLast(new HttpRequestDecoder())
                                .addLast("httpAggregator", new HttpObjectAggregator(1024 * 1024))
                                .addLast(new HttpServerHandler(selector));
                    }
                });
        ChannelFuture cf = bootstrap.bind(config.getPort()).sync();
        logger.debug("HTTP服务器启动成功，绑定端口：{}", config.getPort());
        serverChannel = cf.channel();
    }

    /**
     * 启动WebSocket Client服务器
     * @param config 任务节点配置
     * @throws Exception 顶级异常
     */
    private void bootstrapWebSocketClient(TaskNodeConfig config) throws Exception {
        String url = WS_URI.replace("{id}", UUID.randomUUID().toString());
        url = url.formatted(
                URLEncoder.encode(config.getNodeName(), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getIntervals()), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getPort()), StandardCharsets.UTF_8)
        );
        URI uri = new URI("ws://" + config.getAddress() + url);
        clientGroup = new NioEventLoopGroup();
        final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        final ControlService controlService = ControlServiceFactory.newControlService();
        final WebSocketClientHandler handler = new WebSocketClientHandler(controlService, handshaker, config);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(clientGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("http-codec", new HttpClientCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65536));
                        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
                        pipeline.addLast("ws-handler", handler);
                    }
                });
        clientChannel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        handler.handshakeFuture().sync();
    }
}