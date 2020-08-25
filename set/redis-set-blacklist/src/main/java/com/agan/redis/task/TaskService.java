package com.agan.redis.task;

import com.agan.redis.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@Slf4j
public class TaskService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 提前先把数据刷新到redis缓存中
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化 ..........");
        List<Integer> blacklist=this.blacklist();
        //this.redisTemplate.delete(Constants.BLACKLIST_KEY);
        blacklist.forEach(t->this.redisTemplate.opsForSet().add(Constants.BLACKLIST_KEY,t));
    }

    /**
     * 模拟100个黑名单
     */
    public List<Integer> blacklist() {
        List<Integer> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        return list;
    }
}
