package ink.task.core.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: lisang
 * @DateTime: 2023-11-14 23:38:48
 * @Description:
 */
public class CustomChannelFutureListener implements ChannelFutureListener {
    private static final Logger logger = LoggerFactory.getLogger(CustomChannelFutureListener.class);
    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (channelFuture.isDone() && channelFuture.isSuccess()) {
            logger.debug("心跳消息发送成功");
        } else {
            channelFuture.channel().close();
            logger.error("消息发送失败，错误消息：{}", channelFuture.cause().getMessage());
        }
    }
}
