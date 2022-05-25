package cn.fzzfrjf.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SingletonFactory {

    private static Map<Class<?>,Object> map = new ConcurrentHashMap<>();
    private SingletonFactory(){}

    public static <T> T getInstance(Class<T> clazz){
        Object instance = map.get(clazz);
        if(instance == null){
            synchronized (SingletonFactory.class){
                if(instance == null){
                    try {
                        instance = clazz.newInstance();
                        map.put(clazz,instance);
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
        return clazz.cast(instance);
    }
}
