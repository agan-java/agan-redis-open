package com.agan.redis.controller;

import com.agan.redis.config.Constants;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@RestController
@Slf4j
@RequestMapping(value = "/random")
public class RandomController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping(value = "/crowd")
    public List<String> crowd() {
        List<String> list=null;
        try {
            //采用redis set数据结构，随机取出10条数据
            list = this.redisTemplate.opsForSet().randomMembers(Constants.CROWD_KEY,10);
            log.info("查询结果：{}", list);
        } catch (Exception ex) {
            //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
            log.error("exception:", ex);
            //TODO 走DB查询
        }
        return list;
    }









    @GetMapping(value = "/weibolist")
    public WeiboList weibolist() {
        WeiboList list=null;
        try {
            //随机取1块数据
            list = (WeiboList)this.redisTemplate.opsForSet().randomMember(Constants.WEIBO_LIST_KEY);
            log.info("查询结果：{}", list);
        } catch (Exception ex) {
            //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
            log.error("exception:", ex);
            //TODO 走DB查询
        }
        return list;
    }
}












