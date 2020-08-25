package com.agan.redis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
public class IdGenerator {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String ID_KEY = "id:generator:cart";

    /**
     * 生成全局唯一id
     */
    public Long incrementId() {
        long n=this.stringRedisTemplate.opsForValue().increment(ID_KEY);
        return n;
    }

}
