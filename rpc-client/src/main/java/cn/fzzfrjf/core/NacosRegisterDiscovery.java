package cn.fzzfrjf.core;

import cn.fzzfrjf.enumeration.RpcError;
import cn.fzzfrjf.exception.RpcException;
import cn.fzzfrjf.loadbalance.LoadBalance;
import cn.fzzfrjf.utils.NacosUtils;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

public class NacosRegisterDiscovery implements RegisterDiscovery {
    private final LoadBalance loadBalance;
    private static final Logger logger = LoggerFactory.getLogger(NacosRegisterDiscovery.class);
    public NacosRegisterDiscovery(LoadBalance loadBalance){
        this.loadBalance = loadBalance;
    }

    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try {
            List<Instance> allInstances = NacosUtils.getAllInstance(serviceName);
            Instance instance = loadBalance.getInstance(allInstances);
            return new InetSocketAddress(instance.getIp(),instance.getPort());
        } catch (NacosException e) {
            logger.error("获取服务注册中心的服务失败：{}",e);
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
    }
}
