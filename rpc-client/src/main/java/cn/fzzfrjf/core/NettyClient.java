package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;

import java.util.concurrent.CompletableFuture;

public class NettyClient implements CommonClient{
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest,String host,int port) {
        return null;
    }
}
