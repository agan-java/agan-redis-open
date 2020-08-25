package com.agan.redis.config;


import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
public class Constants {

    public  static final String CACHE_PV_LIST="pv:list";

    public  static final String CACHE_ARTICLE="article:";

    /**
     * Map<时间块，Map<文章Id,访问量>>
     * =Map<2020-01-12 15:30:00到 15:59:00，Map<文章Id,访问量>>
     * =Map<438560，Map<文章Id,访问量>>
     */
    public  static final  Map<Long, Map<Integer,Integer>> PV_MAP=new ConcurrentHashMap();

}