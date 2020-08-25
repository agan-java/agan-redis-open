package com.agan.redis.controller;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Data
public class WeiboList {
    
    private int id;
    /**
     * 榜单名称
     */
    private String name;

    private List<String> users;

}
 