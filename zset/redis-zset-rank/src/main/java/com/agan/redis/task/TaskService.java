package com.agan.redis.task;

import com.agan.redis.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
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
public class TaskService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     *2. 定时5秒钟，模拟微博的热度刷新（例如模拟点赞 收藏 评论的热度值更新）
     * 3. 定时1小时合并统计 天、周、月的排行榜。
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化 ..........");
//        2. 定时5秒钟，模拟微博的热度刷新（例如模拟点赞 收藏 评论的热度值更新）
        new Thread(()->this.refreshDataHour()).start();
//        3. 定时1小时合并统计 天、周、月的排行榜。
        new Thread(()->this.refreshData()).start();
    }

    /**
     *采用26个英文字母来实现排行，随机为每个字母生成一个随机数作为score
     */
    public void refreshHour(){
        //计算当前的小时key
        long hour=System.currentTimeMillis()/(1000*60*60);
        //为26个英文字母来实现排行，随机为每个字母生成一个随机数作为score
        Random rand = new Random();
        for(int i = 1;i<=26;i++){
            //redis的ZINCRBY 新增这个积分值
            this.redisTemplate.opsForZSet().incrementScore(Constants.HOUR_KEY+hour,String.valueOf((char)(96+i)),rand.nextInt(10));
        }
    }

    /**
     *刷新当天的统计数据
     */
    public void refreshDay(){
        long hour=System.currentTimeMillis()/(1000*60*60);
        List<String> otherKeys=new ArrayList<>();
        //算出近24小时内的key
        for(int i=1;i<23;i++){
            String  key=Constants.HOUR_KEY+(hour-i);
            otherKeys.add(key);
        }
        //把当前的时间key，并且把后推23个小时，共计近24小时，求出并集存入Constants.DAY_KEY中
        //redis ZUNIONSTORE 求并集
        this.redisTemplate.opsForZSet().unionAndStore(Constants.HOUR_KEY+hour,otherKeys,Constants.DAY_KEY);

        //设置当天的key 40天过期，不然历史数据浪费内存
        for(int i=0;i<24;i++){
            String  key=Constants.HOUR_KEY+(hour-i);
            this.redisTemplate.expire(key,40, TimeUnit.DAYS);
        }
        log.info("天刷新完成..........");
    }
    /**
     *刷新7天的统计数据
     */
    public void refreshWeek(){
        long hour=System.currentTimeMillis()/(1000*60*60);
        List<String> otherKeys=new ArrayList<>();
        //算出近7天内的key
        for(int i=1;i<24*7-1;i++){
            String  key=Constants.HOUR_KEY+(hour-i);
            otherKeys.add(key);
        }
        //把当前的时间key，并且把后推24*7-1个小时，共计近24*7小时，求出并集存入Constants.WEEK_KEY中
        this.redisTemplate.opsForZSet().unionAndStore(Constants.HOUR_KEY+hour,otherKeys,Constants.WEEK_KEY);

        log.info("周刷新完成..........");
    }

    /**
     *刷新30天的统计数据
     */
    public void refreshMonth(){
        long hour=System.currentTimeMillis()/(1000*60*60);
        List<String> otherKeys=new ArrayList<>();
        //算出近30天内的key
        for(int i=1;i<24*30-1;i++){
            String  key=Constants.HOUR_KEY+(hour-i);
            otherKeys.add(key);
        }
        //把当前的时间key，并且把后推24*30个小时，共计近24*30小时，求出并集存入Constants.MONTH_KEY中
        this.redisTemplate.opsForZSet().unionAndStore(Constants.HOUR_KEY+hour,otherKeys,Constants.MONTH_KEY);
        log.info("月刷新完成..........");
    }

    /**
     *定时1小时合并统计 天、周、月的排行榜。
     */
    public void refreshData(){
        while (true){
            //刷新当天的统计数据
            this.refreshDay();
//            刷新7天的统计数据
            this.refreshWeek();
//            刷新30天的统计数据
            this.refreshMonth();
            //TODO 在分布式系统中，建议用xxljob来实现定时
            try {
                Thread.sleep(1000*60*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *定时5秒钟，模拟微博的热度刷新（例如模拟点赞 收藏 评论的热度值更新）
     */
    public void refreshDataHour(){
        while (true){
            this.refreshHour();
            //TODO 在分布式系统中，建议用xxljob来实现定时
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
