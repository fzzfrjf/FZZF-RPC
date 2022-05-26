package cn.fzzfrjf.loadbalance;

import java.util.List;

public interface LoadBalance {

    <T> T getInstance(List<T> list);
}
