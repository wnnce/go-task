package com.zeroxn.task.core.handler;

import com.zeroxn.task.core.model.TaskNodeConfig;
import com.zeroxn.task.core.system.ControlInfo;
import com.zeroxn.task.core.system.ControlService;
import com.zeroxn.task.core.system.ControlServiceFactory;
import com.zeroxn.task.core.util.JsonUtils;
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
 * @Description:
 */
public class WebSocketClientHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(WebSocketClientHandler.class);
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
    private final ControlService controlService = ControlServiceFactory.newControlService();
    private final TaskNodeConfig config;
    private final WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;

    public WebSocketClientHandler(WebSocketClientHandshaker handshaker, TaskNodeConfig config) {
        this.handshaker = handshaker;
        this.config = config;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final Channel channel = ctx.channel();
        handshaker.handshake(channel);
        startInfoUpload(channel);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.error("WebSocket服务器断开连接");
        EXECUTOR.shutdownNow();
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
        ctx.close();
        cause.printStackTrace();
    }

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
