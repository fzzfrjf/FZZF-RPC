package cn.fzzfrjf.core;


import cn.fzzfrjf.service.RegisterService;
import cn.fzzfrjf.service.ServerPublisher;
import cn.fzzfrjf.utils.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

public class SocketServer implements CommonServer{

    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private final ServerPublisher serverPublisher;
    private final RegisterService registerService;
    private final String host;
    private final int port;


    public SocketServer(String host,int port){
        this.registerService = new NacosRegisterService();
        this.serverPublisher = SingletonFactory.getInstance(DefaultServerPublisher.class);
        this.host = host;
        this.port = port;
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        int keepAliveTime = 60;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,workQueue,Executors.defaultThreadFactory());
    }


    @Override
    public void start(){
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器成功启动。。。。");
            Socket socket;
            while((socket = serverSocket.accept()) != null){
                logger.info("连接成功，客户端ip为:" + socket.getInetAddress());
                threadPool.execute(new RequestHandlerThread(new RequestHandler(),serverPublisher,socket));
            }
            threadPool.shutdown();
        }catch (IOException e){
            logger.error("服务器启动时发生错误：",e);
        }
    }

    @Override
    public void publishService(List<Object> services) {
        for(Object service:services){
            serverPublisher.addService(service);
            registerService.registry(service.getClass().getCanonicalName(),new InetSocketAddress(host,port));
        }
        start();
    }
}
