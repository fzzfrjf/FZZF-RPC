package cn.fzzfrjf.test;

import cn.fzzfrjf.codec.MyDecoder;
import cn.fzzfrjf.codec.MyEncoder;
import cn.fzzfrjf.core.NettyClientHandler;
import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.serializer.ProtobufSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

public class Test {
    public static void main(String[] args) {
        try {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,5000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyEncoder(new ProtobufSerializer()))
                                    .addLast(new MyDecoder())
                                    .addLast(new NettyClientHandler());
                        }
                    });
            bootstrap.connect(new InetSocketAddress("127.0.0.1", 10000)).addListener((ChannelFutureListener) future ->{
                if(future.isSuccess()){
                    System.out.println("连接成功");
                }else{
                    System.out.println("连接失败");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
