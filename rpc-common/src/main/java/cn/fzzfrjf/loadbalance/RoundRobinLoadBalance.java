package cn.fzzfrjf.loadbalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance{

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
    public <T> T getInstance(List<T> list) {
        int index = getAndIncrement() % list.size();
        return list.get(index);
    }
}
