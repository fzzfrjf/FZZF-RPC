package cn.fzzfrjf.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements CommonServer{

    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    public SocketServer(){
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        int keepAliveTime = 60;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(100);
        threadPool = new ThreadPoolExecutor(corePoolSize,maximumPoolSize,keepAliveTime, TimeUnit.SECONDS,workQueue,Executors.defaultThreadFactory());
    }

    @Override
    public void register(Object service, int port) {
        try(ServerSocket serverSocket = new ServerSocket(port)){
            logger.info("服务器正在启动中.....");
            Socket socket ;
            while( (socket = serverSocket.accept()) != null){
                logger.info("连接成功，客户端ip为:" + socket.getInetAddress());
                threadPool.execute(new WorkThread(service,socket));
            }
        }catch (IOException e){
            logger.error("连接时有错误发生：{}",e);
        }
    }
}
