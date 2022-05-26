package cn.fzzfrjf.test;

import cn.fzzfrjf.core.ClientProxy;
import cn.fzzfrjf.core.SocketClient;
import cn.fzzfrjf.entity.ByeService;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.entity.RpcObject;

public class SocketClientTest {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        ClientProxy proxy = new ClientProxy(socketClient);
        HelloService helloService = (HelloService)proxy.getProxy(HelloService.class);
        ByeService byeService = (ByeService) proxy.getProxy(ByeService.class);
        RpcObject rpcObject = new RpcObject(2,"This is SocketClient!");
        String s = helloService.sayHello(rpcObject);
        String b = byeService.bye(rpcObject);
        System.out.println(s);
        System.out.println(b);
    }
}
