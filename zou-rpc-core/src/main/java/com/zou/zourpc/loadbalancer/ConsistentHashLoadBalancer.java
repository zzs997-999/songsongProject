package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ConsistentHashLoadBalancer implements LoaderBalancer{
    /*
    * 一致性哈希环，存放虚拟节点
    * */
    private final TreeMap<Integer,ServiceMetaInfo>virtualNodes=new TreeMap<>();

    /*
    * 虚拟节点数量
    * */
    private static final int VIRTUAL_NODE_NUM=100;
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        //构建虚拟节点黄
        for (ServiceMetaInfo serviceMetaInfo:serviceMetaInfoList){
            for (int i=0;i<VIRTUAL_NODE_NUM;i++){
                int hash=getHash(serviceMetaInfo.getServiceAddress()+"#"+i);
                virtualNodes.put(hash,serviceMetaInfo);
            }
        }

        //获取调用请求的hash值
        int hash=getHash(requestParams);

        //选择最接近且大于等于调用请求的Hash值节点
        Map.Entry<Integer,ServiceMetaInfo>entry=virtualNodes.ceilingEntry(hash);
        if(entry==null){
            //没有，返回头节点
            entry=virtualNodes.firstEntry();
        }
        return entry.getValue();
    }

    /*
    * hash算法
    * */
    private int getHash(Object key){
        return key.hashCode();
    }
}
