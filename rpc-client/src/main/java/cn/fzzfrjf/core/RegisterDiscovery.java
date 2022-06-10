package cn.fzzfrjf.core;

import java.net.InetSocketAddress;

public interface RegisterDiscovery {

    public InetSocketAddress lookupService(String serviceName);
}
