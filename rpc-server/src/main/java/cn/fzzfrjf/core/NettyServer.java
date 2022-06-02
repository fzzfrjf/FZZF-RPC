package cn.fzzfrjf.core;

import cn.fzzfrjf.codec.MyDecoder;
import cn.fzzfrjf.codec.MyEncoder;
import cn.fzzfrjf.serializer.CommonSerializer;
import cn.fzzfrjf.utils.ShutdownHook;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


public class NettyServer extends AbstractServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private final CommonSerializer serializer;
    public NettyServer(CommonSerializer serializer,String host,int port){
        serverPublisher = new DefaultServerPublisher();
        this.serializer = serializer;
        registerService = new NacosRegisterService();
        this.host = host;
        this.port = port;
        scanServices();
    }
    @Override
    public void start() {
        ShutdownHook.getShutdownHook().addClearHook();
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
                            pipeline.addLast(new IdleStateHandler(30,0,0, TimeUnit.SECONDS))
                                    .addLast(new MyEncoder(serializer))
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
}
