package com.zou.example.comsumer;

import com.zou.example.common.model.User;
import com.zou.example.common.service.UserService;
import com.zou.zourpc.config.RpcConfig;
import com.zou.zourpc.proxy.ServiceProxyFactory;
import com.zou.zourpc.utils.ConfigUtils;

public class EasyComsumerExample {
    public static void main(String[]args){
        RpcConfig rpc= ConfigUtils.loadConfig(RpcConfig.class,"rpc");
        System.out.println(rpc);

        UserService userService= ServiceProxyFactory.getProxy(UserService.class);

        User user=new User();
        user.setName("zou");
        //调用
        User newUser=userService.getUser(user);
        if(newUser!=null){
            System.out.println(newUser.getName());
        }
        else{
            System.out.println("user==null");
        }
    }
}
