package cn.fzzfrjf.core;

import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.service.ServerPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServerPublisher implements ServerPublisher {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServerPublisher.class);
    private final ConcurrentHashMap<String,Object> serviceMap = new ConcurrentHashMap<>();
    private final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    @Override
    public synchronized <T> void publishService(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if(serviceMap.containsKey(serviceName)) return;
        registeredService.add(serviceName);
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if(interfaces.length == 0){
            throw new RpcException(RpcError.SERVICE_NOT_REGISTERED);
        }
        for (Class<?> anInterface : interfaces) {
            serviceMap.put(anInterface.getCanonicalName(),service);
        }
        logger.info("向接口：{}注册服务：{}",interfaces,serviceName);
    }

    @Override
    public synchronized Object getService(String serviceName) {
        Object o = serviceMap.get(serviceName);
        if(null == o){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return o;
    }
}
