package com.zou.zourpc.config;

import lombok.Data;

@Data
//注册中心的配置，在rpc配置中加载
public class RegistryConfig {
    /*
    * 注册中心类别
    * */
    private String registry="etcd";

    /*
    * 地址
    * */
    private String address="http://localhost:2379";

    /*
    * 用户名
    * */
    private String username;

    private String password;

    /*
    * 超时时间
    * */
    private Long timeout=10000L;
}
