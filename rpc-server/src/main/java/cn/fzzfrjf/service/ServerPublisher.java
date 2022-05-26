package cn.fzzfrjf.service;

public interface ServerPublisher {

    <T> void addService(T service);

    Object getService(String serviceName);
}
