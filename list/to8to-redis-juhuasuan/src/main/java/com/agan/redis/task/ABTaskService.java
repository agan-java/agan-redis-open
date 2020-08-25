package com.agan.redis.task;

import com.agan.redis.config.Constants;
import com.agan.redis.controller.Product;
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
public class ABTaskService {

    @Autowired
    private RedisTemplate redisTemplate;



    @PostConstruct
    public void initJHSAB(){
        log.info("启动AB定时器..........");
        new Thread(()->runJhsAB()).start();
    }



    public void runJhsAB() {
        while (true){
            //模拟从数据库读取100件 特价商品，用于加载到聚划算页面
            List<Product> list=this.products();
            //先更新B缓存
            this.redisTemplate.delete(Constants.JHS_KEY_B);
            this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY_B,list);
            //先更新A缓存
            this.redisTemplate.delete(Constants.JHS_KEY_A);
            this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY_A,list);
            try {
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("重新刷新..............");
        }
    }

    /**
     * 模拟从数据库读取100件 特价商品，用于加载到聚划算页面
     */
    public List<Product> products() {
        List<Product> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Random rand = new Random();
            int id= rand.nextInt(10000);
            Product obj=new Product((long) id,"product"+i,i,"detail");
            list.add(obj);
        }
        return list;
    }
}
