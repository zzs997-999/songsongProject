package com.zou.zourpc.serializer;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;

import java.io.*;

public class JdkSerializer implements Serializer{
    @Override
    public <T> byte[] serialize(T object) throws IOException {
//        String JSONstr= JSONUtil.toJsonStr(object);
//        return JSONstr.toCharArray();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream=new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
        return outputStream.toByteArray();
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException {
//        String JSONstr=chars.toString();
//        return JSONUtil.toBean(JSONstr,type);
        ByteArrayInputStream inputStream=new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream=new ObjectInputStream(inputStream);
        try{
            return (T)objectInputStream.readObject();
        }catch(ClassNotFoundException e){
            throw new RuntimeException();
        }finally {
            objectInputStream.close();
        }
    }
}
