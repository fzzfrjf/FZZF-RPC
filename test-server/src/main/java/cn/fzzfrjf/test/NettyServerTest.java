package cn.fzzfrjf.test;


import cn.fzzfrjf.annotation.ServiceScan;
import cn.fzzfrjf.core.NettyServer;



@ServiceScan
public class NettyServerTest {

    public static void main(String[] args) {
        NettyServer server = new NettyServer("127.0.0.1",9999);
        server.start();
    }
}
