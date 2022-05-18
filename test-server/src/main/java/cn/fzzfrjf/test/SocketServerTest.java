package cn.fzzfrjf.test;

import cn.fzzfrjf.core.SocketServer;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.service.HelloServiceImpl;

public class SocketServerTest {
    public static void main(String[] args) {
        SocketServer socketServer = new SocketServer();
        HelloService helloService = new HelloServiceImpl();
        socketServer.register(helloService,9000);
    }
}
