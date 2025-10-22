package com.zou.zourpc.proxy;

import java.lang.reflect.Proxy;

/*
* 作用：根据指定类创建动态代理对象
* */
public class ServiceProxyFactory {

    /*
    * 根据服务类创建动态代理对象
    *
    * @param ServiceClass
    * @param <T>
    * @param
    * */
    public static <T> T getProxy(Class<T>serviceClass){
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(),new Class[]{serviceClass},new ServiceProxy());
    }
}
