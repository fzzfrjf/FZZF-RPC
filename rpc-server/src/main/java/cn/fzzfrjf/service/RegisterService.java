package cn.fzzfrjf.service;

import java.net.InetSocketAddress;

public interface RegisterService {

    void registry(String serviceName, InetSocketAddress inetSocketAddress);

}
