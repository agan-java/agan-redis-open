package com.agan.redis.controller;

import com.agan.redis.config.Constants;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    @GetMapping(value = "/prize")
    public List<Integer> prize(int num) {
        try {
            SetOperations<String, Integer> setOperations= this.redisTemplate.opsForSet();
            //spop命令，即随机返回并删除set中一个元素
            List<Integer> objs = setOperations.pop(Constants.PRIZE_KEY,num);
            log.info("查询结果：{}", objs);
            return  objs;
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return null;
    }


}












