package com.agan.redis.task;

import com.agan.redis.common.Constants;
import com.agan.redis.controller.Content;
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
     *模拟直播间的数据
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化 ..........");
        new Thread(()->this.refreshData()).start();
    }



    /**
     *模拟直播间的数据
     */
    public void refreshRoom(){
        List<Content> contents=new ArrayList<>();
        //模拟直播100房间号 的弹幕数据
        String  key= Constants.ROOM_KEY+100;
        Random rand = new Random();
        for(int i=1;i<=5;i++){
            Content content=new Content();
            int id= rand.nextInt(1000);
            content.setUserId(id);

            int temp= rand.nextInt(100);
            content.setContent("发表"+temp);

            long time=System.currentTimeMillis()/1000;
            this.redisTemplate.opsForZSet().add(key,content,time);

            log.debug("模拟直播间100的发言弹幕数据={}",content);
        }
    }


    /**
     * 模拟5秒一批数据
     */
    public void refreshData(){
        while (true){
            this.refreshRoom();
            //TODO 在分布式系统中，建议用xxljob来实现定时
            try {
                Thread.sleep(1000*5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
