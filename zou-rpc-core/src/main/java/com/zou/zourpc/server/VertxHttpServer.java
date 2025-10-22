package com.zou.zourpc.server;

import io.vertx.core.Vertx;

public class VertxHttpServer implements HttpServer{
    @Override
    public void doStart(int port) {
        //创建Vertx实例
        Vertx vertx=Vertx.vertx();
        //创建http服务器
        io.vertx.core.http.HttpServer server=vertx.createHttpServer();

        //监听并处理请求
        server.requestHandler(new HttpServerHandler());
        //启动监听
        server.listen(port,result->{
            if(result.succeeded()){
                System.out.println("Server is now listening thr port"+port);
            }
            else{
                System.out.println("failed"+result.cause());
            }
        });
    }
}
