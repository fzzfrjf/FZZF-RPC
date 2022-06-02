package cn.fzzfrjf.core;

import cn.fzzfrjf.annotation.Service;
import cn.fzzfrjf.annotation.ServiceScan;
import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.service.RegisterService;
import cn.fzzfrjf.service.ServerPublisher;
import cn.fzzfrjf.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Set;

public abstract class AbstractServer implements CommonServer{
    private static final Logger logger = LoggerFactory.getLogger(AbstractServer.class);

    protected ServerPublisher serverPublisher;
    protected RegisterService registerService;
    protected String host;
    protected int port;


    @Override
    public void scanServices(){
        String mainClassName = ReflectUtils.getClassName();
        Class<?> clazz;
        try{
            clazz = Class.forName(mainClassName);
            if(!clazz.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_ANNOTATION_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.RPC_UNKNOWN_ERROR);
        }
        String basePackage = clazz.getAnnotation(ServiceScan.class).value();
        if("".equals(basePackage)){
            basePackage = mainClassName.substring(0,mainClassName.lastIndexOf("."));
        }
        Set<Class<?>> classes = ReflectUtils.getClasses(basePackage);
        for(Class<?> oneClass : classes){
            if(oneClass.isAnnotationPresent(Service.class)) {
                Object service;
                try {
                    service = oneClass.newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    logger.error("创建" + oneClass + "时发生错误");
                    continue;
                }
                serverPublisher.addService(service);
                Class<?>[] interfaces = service.getClass().getInterfaces();
                for (Class<?> anInterface : interfaces) {
                    registerService.registry(anInterface.getCanonicalName(),new InetSocketAddress(host,port));
                }
            }
        }
    }

    @Override
    public void publishService(List<Object> services) {
        for(Object service : services){
            serverPublisher.addService(service);
            Class<?>[] interfaces = service.getClass().getInterfaces();
            for (Class<?> anInterface : interfaces) {
                registerService.registry(anInterface.getCanonicalName(),new InetSocketAddress(host,port));
            }
        }
        start();
    }
}
