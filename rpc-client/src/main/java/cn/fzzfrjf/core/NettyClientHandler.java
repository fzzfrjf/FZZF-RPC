package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.utils.SingletonFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    private final UnprocessedRequest unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try {
            logger.info("客户端获取到服务端返回的信息：{}",msg);
            unprocessedRequest.complete(msg);
            ctx.channel().close();
        } finally {
            ReferenceCountUtil.release(msg);
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("在信息从服务端返回客户端发生错误");
        cause.printStackTrace();
        ctx.close();
    }
}
