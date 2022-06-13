package cn.fzzfrjf.loadbalance.consistentHash;

import cn.fzzfrjf.loadbalance.LoadBalance;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.HashMap;
import java.util.List;

public class IpHashLoadBalance implements LoadBalance {

    private String address;


    @Override
    public Instance getInstance(List<Instance> list) {
        ConsistentHash ch = new ConsistentHash(list,100);
        HashMap<String, Instance> map = ch.map;
        return map.get(ch.getServer(address));
    }

    public void setAddress(String address){
        this.address = address;
    }



}
