package cn.fzzfrjf.test;


import cn.fzzfrjf.core.SocketServer;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.service.ByeServiceImpl;
import cn.fzzfrjf.service.HelloServiceImpl;


import java.util.ArrayList;
import java.util.List;

public class SocketServerTest {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new ByeServiceImpl();
        SocketServer socketServer = new SocketServer("127.0.0.1",9999);
        List<Object> services = new ArrayList<>();
        services.add(helloService);
        services.add(byeService);
        socketServer.publishService(services);
    }
}
