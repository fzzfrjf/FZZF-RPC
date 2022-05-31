package cn.fzzfrjf.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;

public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    private static final ExecutorService threadPool = ThreadPoolFactory.creatDefaultThreadPool("shutdown-hook-pool");
    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook(){
        return shutdownHook;
    }

    public void addClearHook(){
        logger.info("关闭后将自动注销所有服务。。。");
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            NacosUtils.clearRegistry();
            ThreadPoolFactory.shutdownAll();
        }));
    }
}
