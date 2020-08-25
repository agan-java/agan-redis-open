package com.agan.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@RestController
@Slf4j
public class ViewController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @GetMapping(value = "/view")
    public void view(Integer id) {
        //redis key
        String key="article:"+id;
        //调用redis的increment计数器命令
        long n=this.stringRedisTemplate.opsForValue().increment(key);
        log.info("key={},阅读量为{}",key, n);
    }
}
