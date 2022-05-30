package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.service.ServerPublisher;
import cn.fzzfrjf.utils.ThreadPoolFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);
    private final RequestHandler requestHandler;
    private final ServerPublisher serverPublisher;
    private final ExecutorService threadPool;
    private static final String THREAD_NAME_PREFIX = "netty-server-handler";

    public NettyServerHandler(ServerPublisher serverPublisher){
        this.serverPublisher = serverPublisher;
        requestHandler = new RequestHandler();
        threadPool = ThreadPoolFactory.creatDefaultThreadPool(THREAD_NAME_PREFIX);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
        threadPool.execute(() -> {
            try {
                logger.info("服务器接收到请求：{}",msg);
                Object service = serverPublisher.getService(msg.getInterfaceName());
                Object res = requestHandler.handle(msg, service);
                ChannelFuture future = ctx.writeAndFlush(RpcResponse.success(res, msg.getRequestId()));
                future.addListener(ChannelFutureListener.CLOSE);
            } finally {
                ReferenceCountUtil.release(msg);
            }
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理过程中出现错误");
        cause.printStackTrace();
    }
}
