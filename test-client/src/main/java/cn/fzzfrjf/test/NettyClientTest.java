package cn.fzzfrjf.test;

import cn.fzzfrjf.core.ClientProxy;
import cn.fzzfrjf.core.NettyClient;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.entity.RpcObject;
import cn.fzzfrjf.serializer.ProtobufSerializer;

public class NettyClientTest {
    public static void main(String[] args) {
        NettyClient client = new NettyClient(new ProtobufSerializer());
        ClientProxy proxy = new ClientProxy(client,"127.0.0.1",10000);
        HelloService helloService = (HelloService)proxy.getProxy(HelloService.class);
        ByeService byeService = (ByeService) proxy.getProxy(ByeService.class);
        RpcObject rpcObject = new RpcObject(2,"This is NettyClient!");
        String s = helloService.sayHello(rpcObject);
        System.out.println(s);
        String a = byeService.bye(rpcObject);
        System.out.println(a);
    }
}
