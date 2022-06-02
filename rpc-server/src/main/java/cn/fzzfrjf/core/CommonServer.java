package cn.fzzfrjf.core;

import java.util.List;

public interface CommonServer {
    public void start();

    void publishService(List<Object> services);

    void scanServices();
}
