package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RpcRequest rpcRequest,Object service){
        Object result = null;
        try{
            result = doMethod(rpcRequest,service);
        }catch (InvocationTargetException|IllegalAccessException e){
            logger.error("反射调用时发生错误：",e);
        }
        return result;
    }

    private Object doMethod(RpcRequest rpcRequest , Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
        }catch (NoSuchMethodException e){
            logger.error("调用方法时有错误发生：",e);
            return RpcResponse.fail(rpcRequest.getRequestId());
        }
        return method.invoke(service,rpcRequest.getParameters());
    }
}
