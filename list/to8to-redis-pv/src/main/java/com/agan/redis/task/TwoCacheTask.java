package com.agan.redis.task;

import com.agan.redis.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Map;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@Slf4j
public class TwoCacheTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void cacheTask(){
        log.info("启动定时器：二级缓存消费..........");
        new Thread(()->runCache()).start();
    }

    /**
     * 二级缓存定时器消费
     * 定时器，定时（6分钟），从redis的list数据结构pop弹出Map<文章id，访问量PV>，弹出来做了2件事：
     * 第一件事：先把Map<文章id，访问量PV>，保存到数据库
     * 第二件事：再把Map<文章id，访问量PV>，同步到redis缓存的计数器incr。
     */
    public void runCache() {
        while (true){
            while (this.pop()){

            }

            try {
                //间隔2分钟 执行一遍
                Thread.sleep(1000*60*2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("消费二级缓存，定时刷新..............");
        }
    }


    public boolean pop(){
        //从redis的list数据结构pop弹出Map<文章id，访问量PV>
        ListOperations<String, Map<Integer,Integer>> operations= this.redisTemplate.opsForList();
        Map<Integer,Integer> map= operations.rightPop(Constants.CACHE_PV_LIST);
        log.info("弹出pop={}",map);
        if (CollectionUtils.isEmpty(map)){
            return false;
        }
        // 第一步：先存入数据库
        // TODO: 插入数据库

        //第二步：同步redis缓存
        for (Map.Entry<Integer,Integer> entry:map.entrySet()){
//            log.info("key={},value={}",entry.getKey(),entry.getValue());
            String key=Constants.CACHE_ARTICLE+entry.getKey();
            //调用redis的increment命令
            long n=this.redisTemplate.opsForValue().increment(key,entry.getValue());
//            log.info("key={},pv={}",key, n);
        }
        return true;
    }


}
