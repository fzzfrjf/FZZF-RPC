package cn.fzzfrjf.loadbalance;

import cn.fzzfrjf.extension.SPI;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;


@SPI
public interface LoadBalance {

    Instance getInstance(List<Instance> list);
}
