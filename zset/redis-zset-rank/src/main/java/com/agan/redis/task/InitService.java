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
import java.util.concurrent.TimeUnit;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@Slf4j
public class InitService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 先初始化1个月的历史数据
     */
    public void init30day(){
        //计算当前的小时key
        long hour=System.currentTimeMillis()/(1000*60*60);
        //初始化近30天，每天24个key
        for(int i=1;i<24*30;i++){
            //倒推过去30天
            String  key=Constants.HOUR_KEY+(hour-i);
            this.initMember(key);
            System.out.println(key);
        }
    }

    /**
     *初始化某个小时的key
     */
    public void initMember(String key) {
        Random rand = new Random();
        //采用26个英文字母来实现排行，随机为每个字母生成一个随机数作为score
        for(int i = 1;i<=26;i++){
            this.redisTemplate.opsForZSet().add(key,String.valueOf((char)(96+i)),rand.nextInt(10));
        }
    }

}
