package cn.fzzfrjf.test;

import cn.fzzfrjf.core.DefaultServerPublisher;
import cn.fzzfrjf.core.SocketServer;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.service.HelloServiceImpl;
import cn.fzzfrjf.service.ServerPublisher;

public class SocketServerTest {
    public static void main(String[] args) {
        ServerPublisher serverPublisher = new DefaultServerPublisher();
        HelloService helloService = new HelloServiceImpl();
        serverPublisher.publishService(helloService);
        SocketServer socketServer = new SocketServer(serverPublisher);
        socketServer.start(9000);
    }
}
