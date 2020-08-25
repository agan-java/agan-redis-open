package com.agan.redis.controller;

import com.agan.redis.service.ShortUrlGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@RestController
@Slf4j
public class ShortUrlController {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private RedisTemplate redisTemplate;
    private  final static  String SHORT_URL_KEY="short:url";

    /**
     * 长链接转换为短链接
     * 实现原理：长链接转换为短加密串key，然后存储在redis的hash结构中。
     */
    @GetMapping(value = "/encode")
    public String encode(String url) {
        //一个长链接url转换为4个短加密串key
        String [] keys= ShortUrlGenerator.shortUrl(url);
        //任意取出其中一个，我们就拿第一个
        String key=keys[0];
        //用hash存储，key=加密串，value=原始url
        this.redisTemplate.opsForHash().put(SHORT_URL_KEY,key,url);
        log.info("长链接={}，转换={}",url,key);
        return "http://127.0.0.1:9090/"+key;
    }

    /**
     * 重定向到原始的URL
     * 实现原理：通过短加密串KEY到redis找出原始URL，然后重定向出去
     */
    @GetMapping(value = "/{key}")
    public void decode(@PathVariable String key) {
        //到redis中把原始url找出来
        String url=(String) this.redisTemplate.opsForHash().get(SHORT_URL_KEY,key);
        try {
            //重定向到原始的url
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}












