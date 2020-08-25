package com.agan.redis.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class Controller {

    @Autowired
    private RedisTemplate redisTemplate;




    /**
     * 发送消息
     */
    @GetMapping(value = "/set")
    public  void add(String key ,String value) {
         this.redisTemplate.opsForValue().set(key,value);
    }

    @GetMapping(value = "/get")
    public  String  get(String key ) {
        return (String) this.redisTemplate.opsForValue().get(key);
    }

    @GetMapping(value = "/init")
    public void refreshData(){
        for (int i=0;i<10;i++){
            String key="user:"+i;
            this.redisTemplate.opsForValue().set(key,i);
            log.debug("set key={},value={}",key,i);
        }
    }

}












