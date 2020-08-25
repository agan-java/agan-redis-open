package com.agan.redis.controller;


import com.agan.redis.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    /**
     *第一次进入房间,返回最新的前5条弹幕
     */
    @GetMapping(value = "/goRoom")
    public List<Content> goRoom(Integer roomId,Integer userId){

        List<Content> list= new ArrayList<>();

        String  key= Constants.ROOM_KEY+roomId;
        //进入房间,返回最新的前5条弹幕
        Set<ZSetOperations.TypedTuple<Content>> rang=
                this.redisTemplate.opsForZSet().reverseRangeWithScores(key,0,5);
        for (ZSetOperations.TypedTuple<Content> obj:rang){
            list.add(obj.getValue());
            log.debug("content={},score={}",obj.getValue(),obj.getScore().longValue());
        }

        String userkey=Constants.ROOM_USER_TIME_KEY+userId;
        //把当前的时间T,保持到redis，供下次拉取用
        Long now=System.currentTimeMillis()/1000;
        this.redisTemplate.opsForValue().set(userkey,now);
        return list;
    }

    /**
     *登录房间后 客户端间隔5秒钟来拉取数据
     */
    @GetMapping(value = "/commentList")
    public List<Content>  commentList(Integer roomId,Integer userId){
        List<Content> list= new ArrayList<>();

        String key= Constants.ROOM_KEY+roomId;
        String userkey=Constants.ROOM_USER_TIME_KEY+userId;

        long now=System.currentTimeMillis()/1000;
        //拿取上次的读取时间
        Long ago=Long.parseLong(this.redisTemplate.opsForValue().get(userkey).toString());
        log.debug("查找范围：{}  {}",ago,now);
        //获取上次到现在的数据
        Set<ZSetOperations.TypedTuple<Content>> rang= this.redisTemplate.opsForZSet().rangeByScoreWithScores(key,ago,now);
        for (ZSetOperations.TypedTuple<Content> obj:rang){
            list.add(obj.getValue());
            log.debug("content={},score={}",obj.getValue(),obj.getScore().longValue());
        }

        //把当前的时间T,保持到redis，供下次拉取用
        now=System.currentTimeMillis()/1000;
        this.redisTemplate.opsForValue().set(userkey,now);
        return list;
    }

}
