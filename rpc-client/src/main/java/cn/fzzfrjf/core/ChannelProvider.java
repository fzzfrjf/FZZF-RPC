package cn.fzzfrjf.core;

import cn.fzzfrjf.codec.MyDecoder;
import cn.fzzfrjf.codec.MyEncoder;
import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.serializer.CommonSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChannelProvider {
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();
    private static final int MAX_RETRY_NUMBER = 5;
    private static Channel channel = null;
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    public static final Map<String,Channel> channelMap = new ConcurrentHashMap<>();




    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                .option(ChannelOption.SO_KEEPALIVE,true)
                .option(ChannelOption.TCP_NODELAY,true);
        return bootstrap;
    }


    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        String key = inetSocketAddress.toString() + serializer.getSerializerCode();
        if(channelMap.containsKey(key)){
            Channel oneChannel = channelMap.get(key);
            if(oneChannel != null || oneChannel.isActive()){
                return oneChannel;
            }else{
                channelMap.remove(key);
            }
        }
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new MyEncoder(serializer))
                        .addLast(new IdleStateHandler(0,5,0,TimeUnit.SECONDS))
                        .addLast(new MyDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            connect(bootstrap,inetSocketAddress,countDownLatch);
            countDownLatch.await();
            channelMap.put(key,channel);
        } catch (InterruptedException e) {
            logger.error("获取channel时发生错误");
        }
        return channel;
    }
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,CountDownLatch countDownLatch){
        connect(bootstrap,inetSocketAddress,MAX_RETRY_NUMBER,countDownLatch);
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,int retry,CountDownLatch countDownLatch){
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future ->{
            if(future.isSuccess()){
                logger.info("获取channel连接成功,连接到服务器{}：{}",inetSocketAddress.getAddress(),inetSocketAddress.getPort());
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            if(retry == 0){
                logger.error("达到最大重连次数，连接失败");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            int number = (MAX_RETRY_NUMBER - retry) + 1;
            logger.info("第{}次重连.....",number);
            int delay = 1 << number ;
            bootstrap.config().group().schedule(() -> connect(bootstrap,inetSocketAddress,retry - 1,countDownLatch),delay, TimeUnit.SECONDS);
        });
    }
}
