package com.agan.redis.controller;


import com.agan.redis.client.NettyClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class Controller {

    @Autowired
    private NettyClient nettyClient;


    @GetMapping(value = "/sendAsyncMsg")
    public void sendAsyncMsg(String text) {

        nettyClient.sendMsg(text);
        //拿到服务端的数据？
    }

}
