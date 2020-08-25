package com.agan.redis.task;

import com.agan.redis.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@Slf4j
public class OneCacheTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void cacheTask(){
        log.info("启动定时器：一级缓存消费..........");
        new Thread(()->runCache()).start();
    }




    /**
     * 一级缓存定时器消费
     * 定时器，定时（5分钟）从jvm的map把时间块的阅读pv取出来，
     * 然后push到reids的list数据结构中，list的存储的书为Map<文章id，访问量PV>即每个时间块的pv数据
     */
    public void runCache() {
        while (true){
            this.consumePV();
            try {
                //间隔1.5分钟 执行一遍
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("消费一级缓存，定时刷新..............");
        }
    }


    public void consumePV(){
        //为了方便测试 改为1分钟 时间块
        long m1=System.currentTimeMillis()/(1000*60*1);
        Iterator<Long> iterator= Constants.PV_MAP.keySet().iterator();
        while (iterator.hasNext()){
            //取出map的时间块
            Long key=iterator.next();
            //小于当前的分钟时间块key ，就消费
            if (key<m1){
                //先push
                Map<Integer,Integer> map=Constants.PV_MAP.get(key);
                //push到reids的list数据结构中，list的存储的书为Map<文章id，访问量PV>即每个时间块的pv数据
                this.redisTemplate.opsForList().leftPush(Constants.CACHE_PV_LIST,map);
                //后remove
                Constants.PV_MAP.remove(key);
                log.info("push进{}",map);
            }
        }
    }


}
