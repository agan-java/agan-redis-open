package com.agan.redis.controller;

import com.agan.redis.config.Constants;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@RestController
@Slf4j
@RequestMapping(value = "/random")
public class RandomController {

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping(value = "/prize")
    public String prize() {
        String result="";
        try {
            //随机取1次。
            String object = (String)this.redisTemplate.opsForSet().randomMember(Constants.PRIZE_KEY);
            if (!StringUtils.isEmpty(object)){
                //截取序列号 例如10-1
                int temp=object.indexOf('-');
                int no=Integer.valueOf(object.substring(0,temp));
                switch (no){
                    case 0:
                        result="谢谢参与";
                        break;
                    case 1:
                        result="获得1个京豆";
                        break;
                    case 5:
                        result="获得5个京豆";
                        break;
                    case 10:
                        result="获得10个京豆";
                        break;
                    default:
                        result="谢谢参与";
                }
            }
            log.info("查询结果：{}", object);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return result;
    }


}












