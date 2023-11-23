package ink.task.client;

import ink.task.client.exception.BootstrapException;
import ink.task.client.factory.ControlServiceFactory;
import ink.task.client.factory.ManagerFactory;
import ink.task.client.factory.TaskProcessHandlerFactory;
import ink.task.core.AbstractTaskProcessorHandler;
import ink.task.core.ProcessorManager;
import ink.task.core.TaskProcessorSelector;
import ink.task.core.TaskRunnerManager;
import ink.task.core.model.TaskNodeConfig;
import ink.task.core.handler.HttpServerHandler;
import ink.task.core.handler.WebSocketClientHandler;
import ink.task.core.system.ControlService;
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
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: lisang
 * @DateTime: 2023-11-12 22:09:25
 * @Description: 启动主类，负责启动任务节点
 */
public final class TaskNodeBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(TaskNodeBootstrap.class);
    private static final String WS_URI = "/node/registration/ws/{id}?name=%s&intervals=%s&port=%s";
    private static final ExecutorService executors = Executors.newFixedThreadPool(2);
    private CompletableFuture<Void> clientTask = null;
    private CompletableFuture<Void> serverTask = null;
    public TaskNodeBootstrap start(TaskNodeConfig config) {
        clientTask = CompletableFuture.runAsync(() -> {
            try {
                bootstrapWebSocketClient(config);
            } catch (Exception e) {
                logger.error("无法连接到服务器");
                throw new BootstrapException("WebSocket Client启动失败，" + e.getMessage());
            }
        }, executors);
        serverTask = CompletableFuture.runAsync(() -> {
            try {
                bootstrapHttpServer(config);
            } catch (Exception e) {
                logger.error("Http服务器启动失败");
                throw new BootstrapException("Http服务器启动失败，" + e.getMessage());
            }
        }, executors);
        return this;
    }

    /**
     * 阻塞线程
     */
    public void sync() {
        if (clientTask != null && serverTask != null) {
            CompletableFuture.allOf(clientTask, serverTask).join();
        } else {
            if (clientTask != null) {
                clientTask.cancel(true);
            } else if (serverTask != null) {
                serverTask.cancel(true);
            }
        }
    }
    private void bootstrapHttpServer(TaskNodeConfig config) throws Exception {
        final NioEventLoopGroup bossGroup = config.isSingleMain() ? new NioEventLoopGroup(1) : new NioEventLoopGroup();
        final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        final TaskRunnerManager manager = ManagerFactory.newRunnerManager(config);
        final AbstractTaskProcessorHandler taskHandler = TaskProcessHandlerFactory.newTaskHandler();
        final TaskProcessorSelector selector = new TaskProcessorSelector(manager, taskHandler);
        try {
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
            cf.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    private void bootstrapWebSocketClient(TaskNodeConfig config) throws Exception {
        String url = WS_URI.replace("{id}", UUID.randomUUID().toString());
        url = url.formatted(
                URLEncoder.encode(config.getNodeName(), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getIntervals()), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getPort()), StandardCharsets.UTF_8)
        );
        URI uri = new URI("ws://" + config.getAddress() + url);
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
            final ControlService controlService = ControlServiceFactory.newControlService();
            final WebSocketClientHandler handler = new WebSocketClientHandler(controlService, handshaker, config);
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
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
            Channel channel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
            handler.handshakeFuture().sync();
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}