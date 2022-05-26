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
import java.util.List;

public class NacosUtils {

    private static final String SERVER_ADDRESS = "127.0.0.1:8848";
    private static final Logger logger = LoggerFactory.getLogger(NacosUtils.class);
    private static final NamingService namingService;

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
    }

    public static List<Instance> getAllInstance(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }
}
