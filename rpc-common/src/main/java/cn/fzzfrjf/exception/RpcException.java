package cn.fzzfrjf.exception;

import cn.fzzfrjf.enumeration.RpcError;

public class RpcException extends RuntimeException{

    public RpcException(RpcError rpcError,String detail){
        super(rpcError.getMessage() + detail);
    }

    public RpcException(RpcError rpcError){
        super(rpcError.getMessage());
    }

    public RpcException(RpcError rpcError,Throwable cause){
        super(rpcError.getMessage(),cause);
    }
}
