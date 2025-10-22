package com.zou.zourpc.serializer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zou.zourpc.model.RpcRequest;
import com.zou.zourpc.model.RpcResponse;

import java.io.IOException;

public class JsonSerializer implements Serializer{
    private static final ObjectMapper OBJECT_MAPPER=new ObjectMapper();
    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
        T obj=OBJECT_MAPPER.readValue(bytes,type);
        //泛型编程可能会经历类型擦除
        if(obj instanceof RpcRequest){
            return handleRequest((RpcRequest)obj,type);
        }
        if(obj instanceof RpcResponse){
            return handleResponse((RpcResponse)obj,type);
        }
        return obj;
    }

    private <T> T handleResponse(RpcResponse obj, Class<T> type) throws IOException {
        byte[]dataBytes=OBJECT_MAPPER.writeValueAsBytes(obj.getData());
        obj.setData(OBJECT_MAPPER.readValue(dataBytes,obj.getDataType()));
        return type.cast(obj);
    }

    private <T> T handleRequest(RpcRequest obj, Class<T> type) throws IOException {
        Class<?>[]parameterTypes=obj.getParameterTypes();
        Object[]args=obj.getArgs();

        //循环处理每个参数类型
        for(int i=0;i<parameterTypes.length;i++){
            Class<?>clazz=parameterTypes[i];
            if(!clazz.isAssignableFrom(args[i].getClass())){
                byte[]argsByte=OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i]=OBJECT_MAPPER.readValue(argsByte,clazz);
            }
        }
        return type.cast(obj);
    }
}
