package com.zou.zourpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/*
* 封装服务的注册信息的数据结构
* 服务元信息
* */
@Data
public class ServiceMetaInfo {
    //服务名称
    private String serviceName;

    //服务版本号
    private String serviceVersion="1.0";

    //服务域名
    private String serviceHost;

    //服务端口
    private Integer servicePort;

    //服务分组，暂未实现
    private String serviceGroup="default";

    public String getServiceKey(){
        //获取服务键名
        return String.format("%s:%s",serviceName,serviceVersion);
    }

    public String getServiceNodeKey(){
        return String.format("%s/%s:%s",getServiceKey(),serviceHost,servicePort);
    }

    public String getServiceAddress(){
        if(!StrUtil.contains(serviceHost,"http")){
            return String.format("http://%s:%s",serviceHost,servicePort);
        }
        return String.format("%s:%s",serviceHost,servicePort);
    }
}
