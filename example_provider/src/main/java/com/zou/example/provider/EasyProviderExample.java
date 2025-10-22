package com.zou.example.provider;

import com.zou.example.common.service.UserService;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.constant.RpcConstant;
import com.zou.zourpc.model.ServiceMetaInfo;
import com.zou.zourpc.registry.LocalRegistry;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.server.HttpServer;
import com.zou.zourpc.server.VertxHttpServer;

import java.util.concurrent.ExecutionException;

public class EasyProviderExample {
    public static void main(String[]args){
        //初始化rpc框架
        RpcApplication.init();
        //注册服务到注册中心
        RpcConfig rpcConfig=RpcApplication.getRpcConfig();
        Registry registry= RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
        ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getPort());
        try{
            registry.register(serviceMetaInfo);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //注册服务
        LocalRegistry.register(UserService.class.getName(),UserServiceImpl.class);
        //启动web服务器
        HttpServer httpServer=new VertxHttpServer();
        httpServer.doStart(8080);
    }
}
