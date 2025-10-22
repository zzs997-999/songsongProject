package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.spi.SpiLoader;

public class LoadBalancerFactory {

    static {
        SpiLoader.load(LoaderBalancer.class);
    }

    private static final LoaderBalancer DEFAULT_LOAD_BALANCER=new RoundRobinLoadBanlancer();

    /*
    * 获取实例
    * */
    public static LoaderBalancer getLodeBalancer(String key){
        return SpiLoader.getInstance(LoaderBalancer.class,key);
    }
}
