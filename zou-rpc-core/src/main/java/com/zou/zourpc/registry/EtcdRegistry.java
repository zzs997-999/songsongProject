package com.zou.zourpc.registry;

import cn.hutool.json.JSONUtil;
import com.zou.zourpc.config.RegistryConfig;
import com.zou.zourpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class EtcdRegistry implements Registry{
    private Client client;
    private KV kvclient;
    /*
    * 根节点
    * */
    private static final String ETCD_ROOT_PATH="/rpc/";
    @Override
    public void init(RegistryConfig registryConfig) {
        //初始化两个客户端client
        client=Client.builder().endpoints(registryConfig.getAddress()).
                connectTimeout(Duration.ofMillis(registryConfig.getTimeout())).
                build();
        kvclient=client.getKVClient();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws ExecutionException, InterruptedException {
        //要有一个Leaseclient来设置过期时间
        Lease leaseClient=client.getLeaseClient();

        //创建一个30s租约
        long leaseId=leaseClient.grant(30).get().getID();

        //设置要储存的键值对
        String registryKey=ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey();
        ByteSequence key=ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value=ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo),StandardCharsets.UTF_8);

        //将键值对储存，并设置租约
        PutOption putOption=PutOption.builder().withLeaseId(leaseId).build();
        kvclient.put(key,value,putOption);
    }

    @Override
    public void unregister(ServiceMetaInfo serviceMetaInfo) {
        kvclient.delete(ByteSequence.from(ETCD_ROOT_PATH+serviceMetaInfo.getServiceNodeKey(),StandardCharsets.UTF_8));

    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        //前缀搜索，记得结尾要加上/
        String searchPrefix=ETCD_ROOT_PATH+serviceKey;

        try{
            GetOption getOption=GetOption.builder().isPrefix(true).build();
            List<KeyValue>keyValues=kvclient.get(ByteSequence.from(searchPrefix,StandardCharsets.UTF_8),getOption).get().getKvs();
            //解析服务信息
            return keyValues.stream().map(keyValue -> {
                String value=keyValue.getValue().toString(StandardCharsets.UTF_8);
                return JSONUtil.toBean(value,ServiceMetaInfo.class);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("获取服务列表失败",e);
        }
    }

    @Override
    public void destory() {
        System.out.println("当前节点下辖");
        //释放资源
        if(kvclient!=null){
            kvclient.close();
        }
        if(client!=null){
            client.close();
        }
    }
}
