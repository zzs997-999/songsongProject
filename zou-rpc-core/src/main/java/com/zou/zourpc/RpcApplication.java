package com.zou.zourpc;

import com.zou.zourpc.config.RegistryConfig;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.constant.RpcConstant;
import com.zou.zourpc.constant.RpcConstant.*;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RpcApplication {
    //只加载一次
    private static volatile RpcConfig rpcConfig;

    /*
    * 框架初始化
    * 支持传入自定义配置
    * @param newRpcConfig
    * */
    private static void init(RpcConfig newRpcConfig){
        rpcConfig=newRpcConfig;
        log.info("rpc init,config={}",newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig=rpcConfig.getRegistryConfig();
        Registry registry= RegistryFactory.getRegistry(registryConfig.getRegistry());
        registry.init(registryConfig);
        log.info("registry init,config={}",registryConfig);
    }

    /*
    * 普通初始化
    * */
    public static void init(){
        RpcConfig newRpcConfig;
        try {
            newRpcConfig= ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            //配置加载失败，使用默认值
            newRpcConfig =new RpcConfig();
        }
        init(newRpcConfig);
    }

    /*
    * 获取配置
    * */
    public static RpcConfig getRpcConfig(){
        if(rpcConfig==null){
            synchronized (RpcApplication.class){
                if(rpcConfig==null){
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
