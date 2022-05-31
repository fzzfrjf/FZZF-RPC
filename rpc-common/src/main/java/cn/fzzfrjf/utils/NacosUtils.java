package cn.fzzfrjf.utils;

import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class NacosUtils {

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";
    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);
    private static final NamingService namingService;
    private static final Set<String> serviceNameSet = ConcurrentHashMap.newKeySet();
    private static InetSocketAddress address;

    static {
        namingService = NacosUtils.getNamingService();
    }

    public static NamingService getNamingService(){
        try{
            return NacosFactory.createNamingService(SERVER_ADDRESS);
        } catch (NacosException e) {
            logger.error("连接到nacos失败：{}",e);
            throw new RpcException(RpcError.CONNECT_REGISTRATION_FAILURE);
        }
    }

    public static void register( String serviceName, InetSocketAddress inetSocketAddress) throws NacosException {
        namingService.registerInstance(serviceName,inetSocketAddress.getHostName(),inetSocketAddress.getPort());
        serviceNameSet.add(serviceName);
        address = inetSocketAddress;
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    public static void clearRegistry(){
        if(!serviceNameSet.isEmpty() && address != null){
            Iterator<String> iterator = serviceNameSet.iterator();
            while(iterator.hasNext()){
                String serviceName = iterator.next();
                try {
                    namingService.deregisterInstance(serviceName,address.getHostName(),address.getPort());
                    logger.info("成功注销{}服务",serviceName);
                } catch (NacosException e) {
                    logger.error("注销服务{}失败",serviceName);
                }
            }
        }
    }
}
