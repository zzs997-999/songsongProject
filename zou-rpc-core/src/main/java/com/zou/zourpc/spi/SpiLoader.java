package com.zou.zourpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.zou.zourpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
* spi加载器
* */
@Slf4j
public class SpiLoader {
    /*
    * 储存已加载的类：接口名 （key 实现类）
    * */
    /*
    * 第一个String存接口名，第二个存实现类的名字和具体类
    * */
    private static Map<String, Map<String,Class<?>>>loaderMap=new ConcurrentHashMap<>();

    /*
    * 对象实例化缓存，避免重复new（类路径 对象实例，单例模式）
    * */
    private static  Map<String,Object>instanceCache=new ConcurrentHashMap<>();

    /*
    * 系统SPI目录
    * */
    private static  final String RPC_SYSTEM_SPI_DIR="META-INF/rpc/system/";

    private static  final String RPC_CUSTOM_SPI_DIR="META-INF/rpc/custom/";

    /*
    * 扫描路径
    * */
    private static final String[]SCAN_DIRS=new String[]{RPC_CUSTOM_SPI_DIR,RPC_SYSTEM_SPI_DIR};

    /*
    * 动态加载的类列表
    * */
    private static final List<Class<?>>LOAD_CLASS_LIST= Arrays.asList(Serializer.class);

    /*
    * 加载所有类型
    * */
    public static void loadAll(){
        log.info("加载所有spi");
        for (Class<?>aClass:LOAD_CLASS_LIST){
            load(aClass);
        }
    }

    /*
    * 获取某个接口实例，即某个是个实现类
    * */
    public static <T> T getInstance(Class<?>tClass,String key){
        String tClassName=tClass.getName();
        Map<String,Class<?>>keyClassMap=loaderMap.get(tClassName);
        if(keyClassMap==null){
            throw new RuntimeException(String.format("spiloader未加载%s类型",tClassName));
        }
        if(!keyClassMap.containsKey(key)){
            throw new RuntimeException(String.format("spiloader的%s不存在key=%s类型",tClassName,key));
        }
        //获取要加载的实例类型
        Class<?>implClass=keyClassMap.get(key);
        //从缓存中获取实例
        String implClassName=implClass.getName();
        if(!instanceCache.containsKey(implClassName)){
            try {
                instanceCache.put(implClassName,implClass.newInstance());
            }catch (InstantiationException|IllegalAccessException e){
                String err=String.format("%s实例化是吧",implClassName);
                throw new RuntimeException(err,e);
            }
        }
        return (T)instanceCache.get(implClassName);
    }

    /*
    * 加载某个类型
    * */
    public static Map<String,Class<?>>load(Class<?>loadClass){
        log.info("加载类型为{}的spi",loadClass.getName());
        Map<String,Class<?>>keyClassMap=new HashMap<>();
        for (String scanDIR:SCAN_DIRS){
            List<URL>resources= ResourceUtil.getResources(scanDIR+loadClass.getName());
            //读取每个url文件
            for (URL resource:resources){
                try{
                    InputStreamReader inputStreamReader=new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                    String line;
                    while ((line=bufferedReader.readLine())!=null){
                        String []strArray=line.split("=");
                        if(strArray.length>1){
                            String key=strArray[0];
                            String className=strArray[1];
                            keyClassMap.put(key,Class.forName(className));
                        }
                    }
                }catch (Exception e){
                    log.error("spi resource error",e);
                }
            }
        }
        loaderMap.put(loadClass.getName(),keyClassMap);
        return keyClassMap;
    }
}
