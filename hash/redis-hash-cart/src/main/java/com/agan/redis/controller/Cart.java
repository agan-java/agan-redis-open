package com.agan.redis.controller;

import lombok.Data;

import java.io.Serializable;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Data
public class Cart {

    private Long userId;
    private Long productId;
    private int amount;

}
 