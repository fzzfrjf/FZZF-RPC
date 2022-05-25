package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UnprocessedRequest {
    private static final Logger logger = LoggerFactory.getLogger(UnprocessedRequest.class);
    private static Map<String, CompletableFuture<RpcResponse>> unprocessedMap = new ConcurrentHashMap<>();

    public void put(String rpcRequestId,CompletableFuture<RpcResponse> future){
        unprocessedMap.put(rpcRequestId,future);
    }

    public void remove(String rpcRequestId){
        unprocessedMap.remove(rpcRequestId);
    }

    public void complete(RpcResponse rpcResponse){
        CompletableFuture<RpcResponse> future = unprocessedMap.remove(rpcResponse.getRequestId());
        if(future != null){
            future.complete(rpcResponse);
        }else{
            logger.error("异步获取结果出现错误");
            throw new RpcException(RpcError.COMPLETABLE_ERROR);
        }
    }
}
