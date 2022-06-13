package cn.fzzfrjf.loadbalance.roundRobin;

import cn.fzzfrjf.loadbalance.LoadBalance;
import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private final int getAndIncrement(){
        int current;
        int next;
        do{
            current = this.atomicInteger.get();
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
        }while (!this.atomicInteger.compareAndSet(current,next));
        return next;
    }
    @Override
    public Instance getInstance(List<Instance> list) {
        int index = getAndIncrement() % list.size();
        return list.get(index);
    }
}
