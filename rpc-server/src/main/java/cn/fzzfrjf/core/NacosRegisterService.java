package cn.fzzfrjf.core;

import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.service.RegisterService;
import cn.fzzfrjf.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class NacosRegisterService implements RegisterService {

    private static final Logger logger = LoggerFactory.getLogger(NacosRegisterService.class);



    @Override
    public void registry(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtils.register(serviceName,inetSocketAddress);
        } catch (NacosException e) {
            logger.error("服务注册到注册中心时出现错误：{}",e);
            throw new RpcException(RpcError.REGISTERED_SERVICE_FAILURE);
        }
    }
}
