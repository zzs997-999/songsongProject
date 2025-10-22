package com.zou.zourpc.serializer;

import com.zou.zourpc.spi.SpiLoader;

import java.util.HashMap;
import java.util.Map;

/*
* 序列化器对象是可以服用的，所以用工厂模式+单例模式进行
* */
public class SerializerFactory{

    static {
        SpiLoader.load(Serializer.class);
    }
    /*
    * 序列化映射：实现单例
    * */
//    private static final Map<String,Serializer>KEY_SERIALIZER_MAP=new HashMap<String,Serializer>(){
//        {
//            put(SerializerKeys.JDK,new JdkSerializer());
//            put(SerializerKeys.JSON,new JsonSerializer());
//            put(SerializerKeys.KRYO,new KryoSerializer());
//            put(SerializerKeys.HESSIAN,new HessionSerializer());
//        }
//    };


    /*
    * 默认序列化器
    * */
//    private static final Serializer DEFAULT_SERIALIZER=KEY_SERIALIZER_MAP.get("jdk");
    private static final Serializer DEFAULT_SERIALIZER=new JdkSerializer();
    /*
    * 获取实例
    * */
//    public static Serializer getSerializer(String key){
//        return KEY_SERIALIZER_MAP.getOrDefault(key,DEFAULT_SERIALIZER);
//    }
    public static Serializer getSerializer(String key){
        return SpiLoader.getInstance(Serializer.class,key);
    }


}
