package cn.fzzfrjf.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

public class ThreadPoolFactory {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);

    private static final Map<String,ExecutorService> threadPoolMap = new ConcurrentHashMap<>();
    private static final int CORE_POOL_SIZE = 10;
    public static final int MAXIMUM_POOL_SIZE = 100;
    public static final int KEEPALIVE_TIME = 1;
    public static final int QUEUE_CAPACITY = 100;

    public static ExecutorService creatDefaultThreadPool(String threadNamePrefix){
        return creatDefaultThreadPool(threadNamePrefix,false);
    }

    public static ExecutorService creatDefaultThreadPool(String threadNamePrefix,Boolean daemon){
        ExecutorService pool = threadPoolMap.computeIfAbsent(threadNamePrefix, threadPool -> creatThreadPool(threadNamePrefix,daemon));
        if(pool.isShutdown() || pool.isTerminated()){
            threadPoolMap.remove(threadNamePrefix);
            pool = creatThreadPool(threadNamePrefix,daemon);
            threadPoolMap.put(threadNamePrefix,pool);
        }
        return pool;
    }

    public static void shutdownAll(){
        logger.info("关闭所有线程。。。。");
        threadPoolMap.entrySet().parallelStream().forEach(entry ->{
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池[{}][{}]",entry.getKey(),executorService.isTerminated());
            try{
                executorService.awaitTermination(10,TimeUnit.SECONDS);
            }catch (InterruptedException e){
                logger.error("线程池[{}]关闭失败",entry.getKey());
                executorService.shutdown();
            }
        });
    }


    private static ExecutorService creatThreadPool(String threadNamePrefix,Boolean daemon){
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
        ThreadFactory threadFactory = creatThreadFactory(threadNamePrefix,daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE,MAXIMUM_POOL_SIZE,KEEPALIVE_TIME,TimeUnit.SECONDS,queue,threadFactory);
    }

    private static ThreadFactory creatThreadFactory(String threadNamePrefix,Boolean daemon){
        if(threadNamePrefix != null){
            if(daemon != null){
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            }else{
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }
}
