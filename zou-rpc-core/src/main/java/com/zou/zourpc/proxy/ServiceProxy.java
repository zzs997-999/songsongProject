package com.zou.zourpc.proxy;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zou.zourpc.RpcApplication;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.constant.RpcConstant;
import com.zou.zourpc.loadbalancer.LoadBalancerFactory;
import com.zou.zourpc.loadbalancer.LoaderBalancer;
import com.zou.zourpc.model.RpcRequest;
import com.zou.zourpc.model.RpcResponse;
import com.zou.zourpc.model.ServiceMetaInfo;
import com.zou.zourpc.registry.Registry;
import com.zou.zourpc.registry.RegistryFactory;
import com.zou.zourpc.serializer.JdkSerializer;
import com.zou.zourpc.serializer.Serializer;
import com.zou.zourpc.serializer.SerializerFactory;

import javax.imageio.IIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Serializer serializer=new JdkSerializer();
        final Serializer serializer= SerializerFactory.getSerializer(RpcApplication.getRpcConfig().getSerializer());
        //构造请求
        RpcRequest rpcRequest=RpcRequest.builder().serviceName(method.getDeclaringClass().getName())
                        .methodName(method.getName())
                        .parameterTypes(method.getParameterTypes())
                        .args(args)
                        .build();
        //获取地址

        try{
            byte[]bytes=serializer.serialize(rpcRequest);
            //发送请求
            //todo 注意：这里地址被硬编码了，后期应该靠注册中心发现方法
            //实现
            //从注册中心获取地址
            ServiceMetaInfo serviceMetaInfo=new ServiceMetaInfo();
            RpcConfig rpcConfig=RpcApplication.getRpcConfig();
            Registry registry= RegistryFactory.getRegistry(rpcConfig.getRegistryConfig().getRegistry());
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList=registry.serviceDiscovery(serviceMetaInfo.getServiceName());
            if(CollUtil.isEmpty(serviceMetaInfoList)){
                throw new RuntimeException("暂无服务地址");
            }
            //暂时取第一个服务地址
//            ServiceMetaInfo selectedServiceMetaInfo=serviceMetaInfoList.get(0);
            //负载均衡实现
            LoaderBalancer loaderBalancer= LoadBalancerFactory.getLodeBalancer(rpcConfig.getLoadBalancer());
            //将调用方法名（请求路径）作为负载均衡参数
            Map<String,Object> requestParams=new HashMap<String,Object>();
            requestParams.put("methodName",rpcRequest.getMethodName());
            ServiceMetaInfo selectedServiceMetaInfo=loaderBalancer.select(requestParams,serviceMetaInfoList);
            try(
                HttpResponse httpResponse= HttpRequest.post(selectedServiceMetaInfo.getServiceAddress())
                        .body(bytes)
                        .execute()
            ){
                byte[]result=httpResponse.bodyBytes();
                //反序列化
                RpcResponse rpcResponse=serializer.deserialize(result,RpcResponse.class);
                return rpcResponse.getData();
            }
        }catch (IIOException e){
            e.printStackTrace();
        }
        return null;
    }
}
