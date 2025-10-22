package com.zou.zourpc.config;

import com.zou.zourpc.loadbalancer.LoadBalancerKeys;
import com.zou.zourpc.serializer.SerializerKeys;
import lombok.Data;

@Data
public class RpcConfig {
    /*
    * 名称
    * */
    private String name="zou-rpc";

    /*
    * 版本号
    * */
    private String version="1.0";

    /*
    * 服务器主机名/ip
    * */
    private String serverHost="localhost";

    /*
    * 服务器端口号
    * */
    private Integer port=8080;

    private boolean mock=true;

    /*
    * 序列化器
    * */
    private String serializer= SerializerKeys.HESSIAN;

    /*
    * 注册中心配置
    * */
    private RegistryConfig registryConfig=new RegistryConfig();

    /*
    * 负载均衡器
    * */
    private String loadBalancer= LoadBalancerKeys.ROUND_ROBIN;
}
