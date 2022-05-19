package cn.fzzfrjf.core;


import cn.fzzfrjf.service.ServerPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements CommonServer{

    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    private final ServerPublisher serverPublisher;


    public SocketServer(ServerPublisher serverPublisher){
        this.serverPublisher = serverPublisher;
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        int keepAliveTime = 60;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,workQueue,Executors.defaultThreadFactory());
    }


    @Override
    public void start(int port){
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
}
