package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBanlancer implements LoaderBalancer{

    /*
     * 当前轮询下标
     * */
    private final AtomicInteger currentIndex=new AtomicInteger(0);
    @Override
    public ServiceMetaInfo select(Map<String, Object> requestParams, List<ServiceMetaInfo> serviceMetaInfoList) {
        if(serviceMetaInfoList.isEmpty()){
            return null;
        }
        int size=serviceMetaInfoList.size();
        //只有一个服务，无需轮询
        if(size==1){
            return serviceMetaInfoList.get(0);
        }
        //轮询取模算法
        int index=currentIndex.getAndIncrement()%size;
        return serviceMetaInfoList.get(index);
    }
}
