package com.agan.redis.controller;

import com.agan.redis.config.Constants;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
//@RequestMapping(value = "/")
public class Controller {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 点赞
     */
    @GetMapping(value = "/dolike")
    public String dolike(int postid,int userid) {
        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long object = this.redisTemplate.opsForSet().add(key,userid);
            if (object==1){
                result="点赞成功";
            }else{
                result="你已重复点赞";
            }
            log.info("查询结果：{}", object);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return result;
    }

    /**
     * 取消点赞
     */
    @GetMapping(value = "/undolike")
    public String undolike(int postid,int userid) {
        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long object = this.redisTemplate.opsForSet().remove(key,userid);
            if (object==1){
                result="取消成功";
            }else{
                result="你已重复取消点赞";
            }
            log.info("查询结果：{}", object);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return result;
    }

    /**
     * 根据postid userid查看帖子信息，返回结果是点赞总数和是否点赞
     */
    @GetMapping(value = "/getpost")
    public Map getpost(int postid,int userid) {
        Map map=new HashMap();

        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long size = this.redisTemplate.opsForSet().size(key);
            boolean bo=this.redisTemplate.opsForSet().isMember(key,userid);
            map.put("size",size);
            map.put("isLike",bo);
            log.info("查询结果：{}", map);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return map;
    }

    /**
     * 查看点赞明细，就是有哪些人
     */
    @GetMapping(value = "/likedetail")
    public Set likedetail(int postid) {
        Set set=null;
        try {
            String key=Constants.LIKE_KEY+postid;
            set = this.redisTemplate.opsForSet().members(key);
            log.info("查询结果：{}", set);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return set;
    }
}












