package com.zou.zourpc.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.zou.zourpc.model.RpcRequest;
import com.zou.zourpc.model.RpcResponse;
import com.zou.zourpc.serializer.JdkSerializer;
import com.zou.zourpc.serializer.Serializer;

import javax.imageio.IIOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ServiceProxy implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Serializer serializer=new JdkSerializer();

        //构造请求
        RpcRequest rpcRequest=RpcRequest.builder().serviceName(method.getDeclaringClass().getName())
                        .methodName(method.getName())
                        .parameterTypes(method.getParameterTypes())
                        .args(args)
                        .build();
        try{
            byte[]bytes=serializer.serialize(rpcRequest);
            //发送请求
            //todo 注意：这里地址被硬编码了，后期应该靠注册中心发现方法
            try(
                HttpResponse httpResponse= HttpRequest.post("http://localhost:8080")
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
