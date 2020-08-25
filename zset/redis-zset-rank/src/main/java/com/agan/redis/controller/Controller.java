package com.agan.redis.controller;

import com.agan.redis.config.Constants;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.util.CollectionUtils;
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
public class Controller {

    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping(value = "/getHour")
    public Set getHour() {
        long hour=System.currentTimeMillis()/(1000*60*60);
        //ZREVRANGE 返回有序集key中，指定区间内的成员,降序。
        Set<ZSetOperations.TypedTuple<Integer>> rang= this.redisTemplate.opsForZSet().reverseRangeWithScores(Constants.HOUR_KEY+hour,0,30);
        return rang;
    }
    @GetMapping(value = "/getDay")
    public Set getDay() {
        Set<ZSetOperations.TypedTuple<Integer>> rang= this.redisTemplate.opsForZSet().reverseRangeWithScores(Constants.DAY_KEY,0,30);
        return rang;
    }

    @GetMapping(value = "/getWeek")
    public Set getWeek() {
        Set<ZSetOperations.TypedTuple<Integer>> rang= this.redisTemplate.opsForZSet().reverseRangeWithScores(Constants.WEEK_KEY,0,30);
        return rang;
    }

    @GetMapping(value = "/getMonth")
    public Set getMonth() {
        Set<ZSetOperations.TypedTuple<Integer>> rang= this.redisTemplate.opsForZSet().reverseRangeWithScores(Constants.MONTH_KEY,0,30);
        return rang;
    }
}












