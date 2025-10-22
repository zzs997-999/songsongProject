package com.zou.zourpc.server;

public interface HttpServer {
    /*
    * http服务器接口
    * 启动服务器
    *@parm port
    *
    * 为什么要写这个接口：这个项目实现的是vert。x的web服务器，后续可能会拓展别的服务器
    * 用个接口初始化web服务器，可拓展性强
    * */
    public void doStart(int port);
}
