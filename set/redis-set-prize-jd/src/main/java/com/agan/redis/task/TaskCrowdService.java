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
public class TaskCrowdService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *提前先把数据刷新到redis缓存中。
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化..........");

        boolean bo=this.redisTemplate.hasKey(Constants.PRIZE_KEY);
        if(!bo){
            List<String> crowds=this.prize();
            crowds.forEach(t->this.redisTemplate.opsForSet().add(Constants.PRIZE_KEY,t));
        }
    }

    /**
     *按一定的概率初始化奖品
     */
    public List<String> prize() {
        List<String> list=new ArrayList<>();
        //10个京豆，概率10%
        for (int i = 0; i < 10; i++) {
            list.add("10-"+i);
        }
        //5个京豆，概率20%
        for (int i = 0; i < 20; i++) {
            list.add("5-"+i);
        }
        //1个京豆，概率60%
        for (int i = 0; i < 60; i++) {
            list.add("1-"+i);
        }
        //0个京豆，概率10%
        for (int i = 0; i < 10; i++) {
            list.add("0-"+i);
        }
        return list;
    }
}
