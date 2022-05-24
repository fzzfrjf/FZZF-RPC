package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ClientProxy implements InvocationHandler {

    private CommonClient client;
    private String host;
    private int port;

    public ClientProxy(CommonClient client,String host,int port){
        this.client = client;
        this.host = host;
        this.port = port;
    }

    public Object getProxy(Class<?> clazz){
        return Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},this);
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .interfaceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .build();
        //CompletableFuture<RpcResponse> result = null;
//        if(client instanceof SocketClient){
//            return ((RpcResponse) client.sendRequest(rpcRequest,host,port)).getData();
//        }
//        if(client instanceof NettyClient){
//            result = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest,host,port);
//        }
//      return result.get();
        return ((RpcResponse) client.sendRequest(rpcRequest,host,port)).getData();
    }
}
