package ink.task.spring;

import ink.task.core.handler.HttpServerHandler;
import ink.task.core.handler.WebSocketClientHandler;
import ink.task.core.model.TaskNodeConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Author: lisang
 * @DateTime: 2023-11-23 11:46:45
 * @Description: Netty启动类，在SpringBoot启动后执行
 */
public class BootstrapRunner implements ApplicationRunner, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(BootstrapRunner.class);
    private static final String WS_URI = "/node/registration/ws/{id}?name=%s&intervals=%s&port=%s";
    private final TaskNodeConfig config;
    private final HttpServerHandler serverHandler;
    private final WebSocketClientHandler clientHandler;
    private final NioEventLoopGroup bossGroup;
    private final NioEventLoopGroup workerGroup;
    private final NioEventLoopGroup clientGroup;
    private Channel serverChannel;
    private Channel clientChannel;
    public BootstrapRunner(TaskNodeConfig config, HttpServerHandler serverHandler, WebSocketClientHandler clientHandler) {
        this.config = config;
        this.serverHandler = serverHandler;
        this.clientHandler = clientHandler;
        this.bossGroup = config.isSingleMain() ? new NioEventLoopGroup(1) : new NioEventLoopGroup();
        this.workerGroup = new NioEventLoopGroup();
        this.clientGroup = new NioEventLoopGroup();
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {
        bootstrapWebsocketClient();
        bootstrapHttpServer();
        logger.info("Go-Task Client Success Bootstrap");
    }
    @Override
    public void destroy() throws Exception {
        if (serverChannel != null) {
            serverChannel.close();
            logger.info("关闭Http服务器");
        }
        if (clientChannel != null) {
            clientChannel.close();
            logger.info("关闭WebSocket客户端");
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        clientGroup.shutdownGracefully();
    }

    private void bootstrapWebsocketClient() throws Exception {
        String url = WS_URI.replace("{id}", UUID.randomUUID().toString());
        url = url.formatted(
                URLEncoder.encode(config.getNodeName(), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getIntervals()), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(config.getPort()), StandardCharsets.UTF_8)
        );
        URI uri = new URI("ws://" + config.getAddress() + url);
        final WebSocketClientHandshaker handshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        clientHandler.setHandshaker(handshaker);
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
                        pipeline.addLast("ws-handler", clientHandler);
                    }
                });
        clientChannel = bootstrap.connect(uri.getHost(), uri.getPort()).sync().channel();
        clientHandler.handshakeFuture().sync();
    }
    private void bootstrapHttpServer() throws Exception {
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
                                .addLast(serverHandler);
                    }
                });
        ChannelFuture cf = bootstrap.bind(config.getPort()).sync();
        logger.debug("HTTP服务器启动成功，绑定端口：{}", config.getPort());
        serverChannel = cf.channel();
    }
}
