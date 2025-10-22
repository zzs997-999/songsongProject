package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomLoadBanlancer implements LoaderBalancer{
    private final Random random=new Random();
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
        return serviceMetaInfoList.get(random.nextInt(size));
    }
}
