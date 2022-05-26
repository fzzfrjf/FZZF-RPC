package cn.fzzfrjf.core;

import cn.fzzfrjf.codec.MyDecoder;
import cn.fzzfrjf.codec.MyEncoder;
import cn.fzzfrjf.serializer.CommonSerializer;
import cn.fzzfrjf.serializer.ProtobufSerializer;
import cn.fzzfrjf.service.RegisterService;
import cn.fzzfrjf.service.ServerPublisher;
import cn.fzzfrjf.utils.NacosUtils;
import cn.fzzfrjf.utils.SingletonFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;


public class NettyServer implements CommonServer{

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final ServerPublisher serverPublisher;
    private final CommonSerializer serializer;
    private final RegisterService registerService;
    private final String host;
    private final int port;
    public NettyServer(CommonSerializer serializer,String host,int port){
        this.serverPublisher = SingletonFactory.getInstance(DefaultServerPublisher.class);
        this.serializer = serializer;
        registerService = new NacosRegisterService();
        this.host = host;
        this.port = port;
    }
    @Override
    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss,worker)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG,256)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childOption(ChannelOption.TCP_NODELAY,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new MyEncoder(serializer))
                                    .addLast(new MyDecoder())
                                    .addLast(new NettyServerHandler(serverPublisher));
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host,port).sync();
            future.channel().closeFuture().sync();
        }catch (InterruptedException e){
            logger.error("启动服务时发生错误");
        }
        finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }

    @Override
    public void publishService(List<Object> services) {
        for(Object service : services){
            serverPublisher.addService(service);
            Class<?>[] interfaces = service.getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                registerService.registry(anInterface.getCanonicalName(),new InetSocketAddress(host,port));
            }
        }
        start();
    }
}
