package cn.fzzfrjf.utils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectUtils {


    public static String getClassName(){
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        String className = stackTrace[stackTrace.length - 1].getClassName();
        return className;
    }


    public static Set<Class<?>> getClasses(String packageName){
        Set<Class<?>> setClasses = new LinkedHashSet<>();
        boolean recursive = true;
        String packageDirName = packageName.replace(".","/");
        Enumeration<URL> dirs;
        try{
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);
            while(dirs.hasMoreElements()){
                URL url = dirs.nextElement();
                String protocol = url.getProtocol();
                if("file".equals(protocol)){
                    String filePath = URLDecoder.decode(url.getFile(),"UTF-8");
                    findClassesInPackageByFile(packageName,filePath,recursive,setClasses);
                }else if("jar".equals(protocol)){
                    JarFile jarFile;
                    try{
                        jarFile = ((JarURLConnection)url.openConnection()).getJarFile();
                        Enumeration<JarEntry> entries = jarFile.entries();
                        finaClassesInPackageByJar(packageName,entries,packageDirName,recursive,setClasses);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return setClasses;
    }

    private static void findClassesInPackageByFile(String packageName,String packagePath,final boolean recursive,Set<Class<?>> set){
        File dir = new File(packagePath);
        if(!dir.exists() || !dir.isDirectory()){
            return;
        }
        File[] dirFiles = dir.listFiles(pathname -> (recursive || pathname.isDirectory()) || (pathname.getName().endsWith(".class")));
        for(File file:dirFiles){
            if(file.isDirectory()){
                findClassesInPackageByFile(packageName + "." + file.getName(), file.getAbsolutePath(), recursive,set);
            }else{
                String className = file.getName().substring(0,file.getName().length() - 6);
                try {
                    set.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." +className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void finaClassesInPackageByJar(String packageName, Enumeration<JarEntry> entries,String packageDirName , final boolean recursive,Set<Class<?>> set){
        while(entries.hasMoreElements()){
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if(name.charAt(0) == '/'){
                name = name.substring(1);
            }
            if(name.startsWith(packageDirName)){
                int idx = name.lastIndexOf("/");
                if(idx != -1){
                    packageName = name.substring(0,idx).replace("/",".");
                }
                if(idx != -1 || recursive){
                    if(name.endsWith(".class") && !jarEntry.isDirectory()){
                        String className = name.substring(packageName.length()+1,name.length() - 6);
                        try {
                            set.add(Thread.currentThread().getContextClassLoader().loadClass(packageName + "." + className));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
