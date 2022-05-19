package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import cn.fzzfrjf.entity.RpcResponse;
import cn.fzzfrjf.service.ServerPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandlerThread implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private final RequestHandler handler;
    private final ServerPublisher serverPublisher;
    private final Socket socket;

    public RequestHandlerThread(RequestHandler handler,ServerPublisher serverPublisher,Socket socket){
        this.handler = handler;
        this.serverPublisher = serverPublisher;
        this.socket = socket;
    }

    @Override
    public void run() {
        try(ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            RpcRequest rpcRequest = (RpcRequest) ois.readObject();
            Object service = serverPublisher.getService(rpcRequest.getInterfaceName());
            Object result = handler.handle(rpcRequest, service);
            oos.writeObject(RpcResponse.success(result,rpcRequest.getRequestId()));
            oos.flush();
        }catch (IOException | ClassNotFoundException e){
            logger.error("调用或发送时发生错误：",e);
        }
    }
}
