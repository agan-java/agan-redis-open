package com.agan.redis.controller;


import com.agan.redis.client.NettyClient;
import com.agan.redis.service.NettyClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    @Autowired
    private NettyClientService clientService;


    @GetMapping(value = "/sendSyncMsg")
    public String sendSyncMsg(String text) {
        String result = clientService.sendSyncMsg(text);
        log.info("异步变同步的结果: {}",result);
        //步骤6.客户端请求线程被唤醒后，从Future中拿到响应结果，然后做业务处理。
        return "result:"+result ;
    }

}
