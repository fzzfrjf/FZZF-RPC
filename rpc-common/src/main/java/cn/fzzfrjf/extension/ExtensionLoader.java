package cn.fzzfrjf.extension;

import cn.fzzfrjf.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtensionLoader<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    /**
     *扩展类存放的地址
     */
    private static final String SERVICE_DIRECTORY = "META-INF/extensions/";
    /**
     *扩展加载器的缓存
     */
    private static final Map<Class<?>,ExtensionLoader<?>> EXTENSION_LOADER = new ConcurrentHashMap<>();
    /**
     *扩展类实例的缓存
     */
    private static final Map<Class<?>,Object> EXTENSION_INSTANCE = new ConcurrentHashMap<>();


    /**
     *扩展类Class文件
     */
    private final Class<?> type;
    /**
     *扩展类实例持有者Holder的缓存
     */
    private final Map<String,Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    /**
     *扩展类配置列表缓存
     */
    private final Holder<Map<String,Class<?>>> cachedClass = new Holder<>();

    /**
     *构造器指定Class类型
     */
    public ExtensionLoader(Class<?> type){this.type = type;}

    /**
    * @Description: 根据类型得到扩展加载器
    * @Param: Class文件
    * @return: 扩展类型加载器
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    public static <S> ExtensionLoader<S> getExtensionLoader(Class<S> type){
        if(type == null){
            throw new IllegalArgumentException("Extension type should not be null");
        }
        if(!type.isInterface()){
            throw new IllegalArgumentException("Extension type must be an interface");
        }
        if(type.getAnnotation(SPI.class) == null){
            throw new IllegalArgumentException("Extension type must be annotated by @SPI");
        }
        ExtensionLoader<S> extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADER.get(type);
        if(extensionLoader == null){
            EXTENSION_LOADER.putIfAbsent(type,new ExtensionLoader<S>(type));
            extensionLoader = (ExtensionLoader<S>) EXTENSION_LOADER.get(type);
        }
        return extensionLoader;
    }

    /**
    * @Description: 获取扩展实例对象
    * @Param: name 扩展类在配置文件中的名字
    * @return: 扩展类实例对象
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    public T getExtension(String name){
        if(StringUtil.isBlank(name)){
            throw new IllegalArgumentException("Extension name should not be null or empty");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if(holder == null){
            cachedInstances.putIfAbsent(name,new Holder<>());
            holder = cachedInstances.get(name);
        }
        Object instance = holder.getValue();
        if(instance == null){
            synchronized (holder){
                if(instance == null){
                    instance = createExtension(name);
                    holder.setValue(instance);
                }
            }
        }
        return (T) instance;
    }

    /**
    * @Description: 创建对应名字的扩展类对象
    * @Param: name 扩展类在配置文件中的名字
    * @return: 扩展类对象
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    private T createExtension(String name){
        Map<String, Class<?>> extensionClasses = getExtensionClasses();
        Class<?> clazz = extensionClasses.get(name);
        if(clazz == null){
            throw new RuntimeException("No such extension of name:" + name);
        }
        T instance = (T) EXTENSION_INSTANCE.get(clazz);
        if(instance == null){
            try{
                EXTENSION_INSTANCE.putIfAbsent(clazz,clazz.newInstance());
                instance = (T) EXTENSION_INSTANCE.get(clazz);
            }catch (Exception e){
                logger.error(e.getMessage());
                throw new RuntimeException("Fail to create instance of the extension class:" + clazz);
            }
        }
        return instance;
    }

    /**
    * @Description: 获取当前类型的所有扩展类
    * @Param: 
    * @return: 扩展类名字与对应的全类名的Map
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    private Map<String,Class<?>> getExtensionClasses(){
        Map<String, Class<?>> classes = cachedClass.getValue();
        if(classes == null){
            synchronized (cachedClass){
                classes = cachedClass.getValue();
                if(classes == null){
                    classes = new HashMap<>();
                    loadDirectory(classes);
                    cachedClass.setValue(classes);
                }
            }
        }
        return classes;
    }


    /**
    * @Description: 获取指定配置文件中所有的配置
    * @Param: Map<String,Class<?>>
    * @return: void
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    private void loadDirectory(Map<String,Class<?>> extensionClasses){
        String fileName = ExtensionLoader.SERVICE_DIRECTORY +type.getName();
        try{
            Enumeration<URL> urls;
            ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
            urls = classLoader.getResources(fileName);
            if(urls != null){
                while(urls.hasMoreElements()){
                    URL url = urls.nextElement();
                    loadResource(extensionClasses,classLoader,url);
                }
            }
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }

    /**
    * @Description: 读取指定配置文件中不同的实例化对象配置并放入缓存
    * @Param:
    * @return: void
    * @Author: fzzfrjf
    * @Date: 2022/6/13
    */
    private void loadResource(Map<String,Class<?>> extensionClasses,ClassLoader classLoader,URL url){
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))){
            String line;
            while((line = reader.readLine()) != null){
                final int ci = line.indexOf("#");
                if(ci >= 0){
                    line = line.substring(0,ci);
                }
                line = line.trim();
                if(line.length() > 0){
                    try{
                        final int ei = line.indexOf("=");
                        String name = line.substring(0,ei).trim();
                        String clazzName = line.substring(ei + 1).trim();
                        if(name.length() > 0 && clazzName.length() > 0){
                            Class<?> clazz = classLoader.loadClass(clazzName);
                            extensionClasses.put(name,clazz);
                        }
                    }catch (ClassNotFoundException e){
                        logger.error(e.getMessage());
                    }
                }
            }
        }catch (IOException e){
            logger.error(e.getMessage());
        }
    }
}
