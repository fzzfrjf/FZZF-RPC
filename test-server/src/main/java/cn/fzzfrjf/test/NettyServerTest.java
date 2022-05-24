package cn.fzzfrjf.test;

import cn.fzzfrjf.core.DefaultServerPublisher;
import cn.fzzfrjf.core.NettyServer;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.serializer.ProtobufSerializer;
import cn.fzzfrjf.service.ByeServiceImpl;
import cn.fzzfrjf.service.HelloServiceImpl;
import cn.fzzfrjf.service.ServerPublisher;

public class NettyServerTest {

    public static void main(String[] args) {
        ServerPublisher serverPublisher = new DefaultServerPublisher();
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new ByeServiceImpl();
        serverPublisher.publishService(helloService);
        serverPublisher.publishService(byeService);
        NettyServer server = new NettyServer(serverPublisher,new ProtobufSerializer());
        server.start(10000);
    }
}
