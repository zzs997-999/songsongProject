package com.zou.zourpc.server;

import com.zou.zourpc.model.RpcRequest;
import com.zou.zourpc.model.RpcResponse;
import com.zou.zourpc.registry.LocalRegistry;
import com.zou.zourpc.serializer.JdkSerializer;
import com.zou.zourpc.serializer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;


public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest request) {
        //指定序列化器
        final Serializer serializer=new JdkSerializer();

        //记录日志
        System.out.println("Receive request:"+request.method()+" "+request.uri());

        //异步处理http请求
        request.bodyHandler(body->{
            byte[]bytes=body.getBytes();
            RpcRequest rpcRequest=null;
            try{
                rpcRequest=serializer.deserialize(bytes,RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //构造响应结果对象
            RpcResponse rpcResponse=new RpcResponse();
            //如果请求为null，直接返回
            if(rpcRequest==null){
                rpcResponse.setMessage("request is null");
                doResponse(request,rpcResponse,serializer);
                return;
            }

            try{
                //获取要调用的服务实现类，通过反射调用
                Class<?>impleClass= LocalRegistry.get(rpcRequest.getServiceName());
                Method method=impleClass.getMethod(rpcRequest.getMethodName(),rpcRequest.getParameterTypes());
                Object result=method.invoke(impleClass.newInstance(),rpcRequest.getArgs());
                //封装结果
                rpcResponse.setData(result);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }
            doResponse(request,rpcResponse,serializer);
        });

    }

    /*
    * 响应
    *
    *
    * */
    private void doResponse(HttpServerRequest request, RpcResponse rpcResponse, Serializer serializer) {
        HttpServerResponse httpServerResponse=request.response().putHeader("content-type","application/json");

        try{
            //序列化
            byte[]serializered=serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serializered));
        } catch (IOException e) {
            e.printStackTrace();
            httpServerResponse.end(Buffer.buffer());
        }
    }
}
