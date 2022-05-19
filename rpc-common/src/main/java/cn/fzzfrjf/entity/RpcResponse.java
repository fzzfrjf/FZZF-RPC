package cn.fzzfrjf.entity;

import cn.fzzfrjf.enumeration.ResponseCode;
import lombok.Data;

import java.io.Serializable;

@Data
public class RpcResponse<T> implements Serializable {

    private int code;

    private String requestId;

    private T data;

    public static <T> RpcResponse<T> success(T data,String requestId){
        RpcResponse<T> rpcResponse = new RpcResponse<>();
        rpcResponse.setCode(ResponseCode.SUCCESS.getCode());
        rpcResponse.setRequestId(requestId);
        rpcResponse.setData(data);
        return rpcResponse;
    }

    public static <T>RpcResponse<T> fail(String requestId){
        RpcResponse<T> rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(ResponseCode.FAILURE.getCode());
        return rpcResponse;
    }
}
