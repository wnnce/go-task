package ink.task.core.handler;

import ink.task.core.model.TaskInfo;
import ink.task.core.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-12 22:42:22
 * @Description: 自定义Http请求处理器
 */
public final class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);
    private static final String TASK_URI = "/task/issued";

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("连接处理异常，错误消息：{}", cause.getMessage());
    }

    /**
     * 处理调度中心下发任务
     * @param ctx 连接上下文
     * @param fullHttpRequest 完整的请求连接
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {
        final String uri = fullHttpRequest.uri();
        FullHttpResponse response;
        if (TASK_URI.equals(uri)) {
            if (fullHttpRequest.method().equals(HttpMethod.POST)) {
                final ByteBuf content = fullHttpRequest.content();
                final String requestBody = content.toString(CharsetUtil.UTF_8);
                TaskInfo taskInfo = JsonUtils.form(requestBody, TaskInfo.class);
                if (taskInfo == null) {
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST);
                } else {

                    logger.info(taskInfo.toString());
                    response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                }
            } else {
                response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.METHOD_NOT_ALLOWED);
            }
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        }
        ChannelFuture future = ctx.writeAndFlush(response);
        future.addListener(ChannelFutureListener.CLOSE);
    }
}
