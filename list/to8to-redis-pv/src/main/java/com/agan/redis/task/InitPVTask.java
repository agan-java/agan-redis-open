package com.agan.redis.task;

import com.agan.redis.config.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@Slf4j
public class InitPVTask {

    @Autowired
    private RedisTemplate redisTemplate;



    @PostConstruct
    public void initPV(){
        log.info("启动模拟大量PV请求 定时器..........");
        new Thread(()->runArticlePV()).start();
    }


    /**
     * 模拟大量PV请求
     */
    public void runArticlePV() {
        while (true){
            this.batchAddArticle();
            try {
                //5秒执行一次
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 对1000篇文章，进行模拟请求PV
     */
    public void   batchAddArticle() {
        for (int i = 0; i < 1000; i++) {
            this.addPV(new Integer(i));
        }
    }

    /**
     *那如何切割时间块呢？ 如何把当前的时间切入时间块中？
     * 例如,我们要计算“小时块”，先把当前的时间转换为为毫秒的时间戳，然后除以一个小时，
     * 即当前时间T/1000*60*60=小时key，然后用这个小时序号作为key。
     * 例如：
     * 2020-01-12 15:30:00=1578814200000毫秒 转换小时key=1578814200000/1000*60*60=438560
     * 2020-01-12 15:59:00=1578815940000毫秒 转换小时key=1578815940000/1000*60*60=438560
     * 2020-01-12 16:30:00=1578817800000毫秒 转换小时key=1578817800000/1000*60*60=438561
     * 剩下的以此类推
     *
     * 每一次PV操作时，先计算当前时间是那个时间块，然后存储Map中。
     */
    public void addPV(Integer id) {
        //生成环境：时间块为5分钟
        //long m5=System.currentTimeMillis()/(1000*60*5);
        //为了方便测试 改为1分钟 时间块
        long m1=System.currentTimeMillis()/(1000*60*1);
        Map<Integer,Integer> mMap=Constants.PV_MAP.get(m1);
        if (CollectionUtils.isEmpty(mMap)){
            mMap=new ConcurrentHashMap();
            mMap.put(id,new Integer(1));
            //<1分钟的时间块，Map<文章Id,访问量>>
            Constants.PV_MAP.put(m1, mMap);
        }else {
            //通过文章id 取出浏览量
            Integer value=mMap.get(id);
            if (value==null){
                mMap.put(id,new Integer(1));
            }else{
                mMap.put(id,value+1);
            }
        }
    }


}
