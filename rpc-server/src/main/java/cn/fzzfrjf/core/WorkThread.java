package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class WorkThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(WorkThread.class);

    private Object service;
    private Socket socket;
    public WorkThread(Object service,Socket socket){
        this.socket = socket;
        this.service = service;
    }
    @Override
    public void run() {
        try(ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
            Object result = method.invoke(service,rpcRequest.getParameters());
            objectOutputStream.writeObject(new RpcResponse().success(result,rpcRequest.getRequestId()));
            objectOutputStream.flush();
        }catch (IOException | ClassNotFoundException | InvocationTargetException |NoSuchMethodException | IllegalAccessException e){
            logger.error("调用或发送时有错误发生：",e);
        }
    }
}
