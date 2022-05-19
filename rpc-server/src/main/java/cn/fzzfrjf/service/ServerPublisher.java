package cn.fzzfrjf.service;

public interface ServerPublisher {

    <T> void publishService(T service);

    Object getService(String serviceName);
}
