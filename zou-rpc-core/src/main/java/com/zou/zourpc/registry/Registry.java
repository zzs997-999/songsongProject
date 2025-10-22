package com.zou.zourpc.registry;

import com.zou.zourpc.config.RegistryConfig;
import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.concurrent.ExecutionException;

public interface Registry {
    /*
    * 初始化
    * */
    void init(RegistryConfig registryConfig);

    /*
    * 注册
    * */
    void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException;

    /*
    * 注销服务
    * */
    void unregister(ServiceMetaInfo serviceMetaInfo);

    /*
    * 发现服务
    * */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /*
    * 服务销毁
    * */
    void destory();
}
