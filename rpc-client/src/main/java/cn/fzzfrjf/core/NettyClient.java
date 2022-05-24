package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.serializer.CommonSerializer;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


public class NettyClient implements CommonClient{
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final CommonSerializer serializer;
    public NettyClient(CommonSerializer serializer){
        this.serializer = serializer;
    }


    @Override
    public Object sendRequest(RpcRequest rpcRequest, String host, int port) {
        Channel channel = ChannelProvider.get(new InetSocketAddress(host,port),serializer);
        try {
            channel.writeAndFlush(rpcRequest).addListener(future -> {
                if(future.isSuccess()){
                    logger.info("成功向服务端发送请求：{}",rpcRequest);
                }else{
                    logger.error("向服务器发送消息失败",future.cause());
                }
            });
            channel.closeFuture().sync();
            AttributeKey<RpcResponse> key = AttributeKey.valueOf(rpcRequest.getRequestId());
            RpcResponse rpcResponse = channel.attr(key).get();
            return rpcResponse;
        } catch (InterruptedException e) {
            logger.error("发送消息时有错误发生：{}",e);
        }
        return null;
    }
}
