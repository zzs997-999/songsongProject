package com.zou.zourpc.loadbalancer;

import com.zou.zourpc.model.ServiceMetaInfo;

import java.util.List;
import java.util.Map;

public interface LoaderBalancer {

    /*
    * 主要功能是获取一个服务调用
    * @param requestParams 请求参数
    * @params serviceMetaInfoList 可用服务列表
    * @return MetaInfo
    * */
    ServiceMetaInfo select(Map<String,Object> requestParams, List<ServiceMetaInfo>serviceMetaInfoList);
}
