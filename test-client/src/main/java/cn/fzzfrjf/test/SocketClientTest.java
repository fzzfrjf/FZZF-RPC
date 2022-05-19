package cn.fzzfrjf.test;

import cn.fzzfrjf.core.ClientProxy;
import cn.fzzfrjf.core.SocketClient;
import cn.fzzfrjf.entity.HelloService;
import cn.fzzfrjf.entity.RpcObject;

public class SocketClientTest {
    public static void main(String[] args) {
        SocketClient socketClient = new SocketClient();
        ClientProxy proxy = new ClientProxy(socketClient,"127.0.0.1",9000);
        HelloService service = (HelloService)proxy.getProxy(HelloService.class);
        RpcObject rpcObject = new RpcObject(2,"This is SocketClient!");
        String s = service.sayHello(rpcObject);
        System.out.println(s);
    }
}
