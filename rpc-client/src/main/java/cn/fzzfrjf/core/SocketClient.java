package cn.fzzfrjf.core;

import cn.fzzfrjf.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient implements CommonClient{
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    @Override
    public Object sendRequest(RpcRequest rpcRequest,String host,int port) {
        try(Socket socket = new Socket(host,port)){
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(rpcRequest);
            oos.flush();
            return ois.readObject();
        }catch (IOException | ClassNotFoundException e){
            logger.error("调用时有错误发生：",e);
            return null;
        }
    }
}
