package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.loadbalance.IpHashLoadBalance;
import cn.fzzfrjf.loadbalance.RoundRobinLoadBalance;
import cn.fzzfrjf.serializer.CommonSerializer;
import cn.fzzfrjf.utils.SingletonFactory;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;


public class NettyClient implements CommonClient{
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    private final CommonSerializer serializer;
    private final UnprocessedRequest unprocessedRequest = SingletonFactory.getInstance(UnprocessedRequest.class);
    private final RegisterDiscovery registerDiscovery;
    public NettyClient(CommonSerializer serializer){
        this.serializer = serializer;
        registerDiscovery = new NacosRegisterDiscovery(new IpHashLoadBalance());
    }


    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        InetSocketAddress inetSocketAddress = registerDiscovery.lookupService(rpcRequest.getInterfaceName());
        Channel channel = ChannelProvider.get(inetSocketAddress,serializer);
        CompletableFuture<RpcResponse> future = new CompletableFuture<>();
        unprocessedRequest.put(rpcRequest.getRequestId(),future);
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
            if(future1.isSuccess()){
                logger.info("成功向服务器发送请求：{}",rpcRequest);
            }else{
                future1.channel().close();
                logger.error("向服务器发送消息失败：{}",future1.cause());
                future.completeExceptionally(future1.cause());
            }
        });
        return future;
    }
}
