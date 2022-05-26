package cn.fzzfrjf.test;

import cn.fzzfrjf.core.DefaultServerPublisher;
import cn.fzzfrjf.core.NettyServer;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.serializer.ProtobufSerializer;
import cn.fzzfrjf.service.ByeServiceImpl;
import cn.fzzfrjf.service.HelloServiceImpl;
import cn.fzzfrjf.service.ServerPublisher;

import java.util.ArrayList;
import java.util.List;

public class NettyServerTest {

    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ByeService byeService = new ByeServiceImpl();
        NettyServer server = new NettyServer(new ProtobufSerializer(),"127.0.0.1",9999);
        List<Object> services = new ArrayList<>();
        services.add(helloService);
        services.add(byeService);
        server.publishService(services);
    }
}
