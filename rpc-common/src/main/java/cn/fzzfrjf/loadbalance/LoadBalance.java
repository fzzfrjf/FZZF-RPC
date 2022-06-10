package cn.fzzfrjf.loadbalance;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

public interface LoadBalance {

    Instance getInstance(List<Instance> list);
}
