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
            List<Integer> crowds=this.prize();
            crowds.forEach(t->this.redisTemplate.opsForSet().add(Constants.PRIZE_KEY,t));
        }
    }

    /**
     * 模拟10个用户来抽奖 list存放的是用户id
     * 例如支付宝参与抽奖，就把用户id加入set集合中
     * 例如公司抽奖，把公司所有的员工，工号都加入到set集合中
     */
    public List<Integer> prize() {
        List<Integer> list=new ArrayList<>();
        for(int i=1;i<=10;i++){
            list.add(i);
        }
        return list;
    }
}
