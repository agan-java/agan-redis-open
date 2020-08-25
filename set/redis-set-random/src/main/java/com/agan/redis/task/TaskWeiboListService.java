package com.agan.redis.task;

import com.agan.redis.config.Constants;
import com.agan.redis.controller.WeiboList;
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
public class TaskWeiboListService {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 定时把数据库的 ，刷新到redis缓存中。
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化 榜单..........");
        List<WeiboList> crowds=this.list();
        this.redisTemplate.delete(Constants.WEIBO_LIST_KEY);
        crowds.forEach(t->this.redisTemplate.opsForSet().add(Constants.WEIBO_LIST_KEY,t));
    }


    /**
     * 模拟10个热门榜单，用于推荐
     */
    public List<WeiboList> list() {
        List<WeiboList> list=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            WeiboList wl=new WeiboList();
            wl.setId(i);
            wl.setName("榜单"+i);
            Random rand = new Random();
            List<String> users=new ArrayList<>();
            for (int j=0;j<3;j++){
                int id= rand.nextInt(10000);
                users.add("user:"+id);
            }
            wl.setUsers(users);
            list.add(wl);
        }
        return list;
    }
}
