package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;

public interface CommonClient {

    Object sendRequest(RpcRequest rpcRequest);
}
