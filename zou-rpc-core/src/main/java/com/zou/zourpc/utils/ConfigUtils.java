package com.zou.zourpc.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

/*
* 配置工具类
* */
public class ConfigUtils {
    /*
    * 加载配置对象
    * @param tClass
    * @param prefix
    * @param <T>
    * */
    public static <T> T loadConfig(Class<T>tClass,String prefix){
        return loadConfig(tClass,prefix,"");
    }

    private static <T> T loadConfig(Class<T> tClass, String prefix, String s) {
        StringBuilder configFileBuilder=new StringBuilder("application");
        if(StrUtil.isNotBlank((s))){
            configFileBuilder.append("-").append(s);
        }
        configFileBuilder.append(".properties");
        Props props=new Props(configFileBuilder.toString());
        return props.toBean(tClass,prefix);
    }
}
