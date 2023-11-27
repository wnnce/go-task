package ink.task.core.handler;

import ink.task.core.model.TaskNodeConfig;
import ink.task.core.system.ControlInfo;
import ink.task.core.system.ControlService;
import ink.task.core.util.JsonUtils;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lisang
 * @DateTime: 2023-11-13 14:38:30
 * @Description: WebSocket连接处理器
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final ControlService controlService;
    private final TaskNodeConfig config;
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(ControlService controlService, TaskNodeConfig config) {
        this.controlService = controlService;
        this.config = config;
    }

    public WebSocketClientHandler(ControlService controlService, WebSocketClientHandshaker handshaker, TaskNodeConfig config) {
        this.controlService = controlService;
        this.handshaker = handshaker;
        this.config = config;
    }

    public void setHandshaker(WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    /**
     * 连接建立时调用的方法，定时上传信息续约
     * @param ctx 连接
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        handshaker.handshake(channel);
        startInfoUpload(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.error("WebSocket服务器断开连接");
        ctx.channel().closeFuture();
        throw new WebSocketClientHandshakeException("服务器连接异常");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            try {
                handshaker.finishHandshake(ch, (FullHttpResponse) msg);
                logger.info("WebSocket Client connected!");
                handshakeFuture.setSuccess();
            } catch (WebSocketHandshakeException e) {
                logger.info("WebSocket Client failed to connect");
                handshakeFuture.setFailure(e);
            }
            return;
        }

        if (msg instanceof FullHttpResponse response) {
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.status() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }

        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame textWebSocketFrame) {
            logger.debug("收到服务器回应，响应消息：{}", textWebSocketFrame.text());
        } else if (frame instanceof PongWebSocketFrame) {
            logger.info("Accept Server pong message");
        } else if (frame instanceof CloseWebSocketFrame) {
            logger.info("Websocket connect close");
            ch.close();
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Websocket连接异常，错误信息：{}", cause.getMessage());
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        // 关闭连接 停止定时任务
        ctx.close();
        EXECUTOR.shutdownNow();
    }

    /**
     * 定时获取信息向通道内发送
     * @param channel 连接通道
     * @throws Exception 异常
     */
    private void startInfoUpload(Channel channel) throws Exception {
        EXECUTOR.scheduleWithFixedDelay(() -> {
            if (channel.isActive()) {
                final ControlInfo systemInfo = controlService.getAllInfo();
                final String message = JsonUtils.to(systemInfo);
                channel.writeAndFlush(new TextWebSocketFrame(message)).addListener(new CustomChannelFutureListener());
            }
        }, 500, config.getIntervals() * 1000L, TimeUnit.MILLISECONDS);
    }
}
