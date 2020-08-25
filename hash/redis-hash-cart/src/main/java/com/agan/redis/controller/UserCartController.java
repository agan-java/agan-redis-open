package com.agan.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@RestController
@Slf4j
@RequestMapping(value = "/cart")
public class UserCartController {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 购物车key的前缀
     */
    public static final String CART_KEY = "cart:user:";

    /**
     * 添加购物车
     */
    @PostMapping(value = "/addCart")
    public void addCart(Cart obj) {
        String key = CART_KEY + obj.getUserId();
        Boolean hasKey = redisTemplate.opsForHash().getOperations().hasKey(key);
        //存在
        if(hasKey){
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
        }else{
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
            this.redisTemplate.expire(key,90, TimeUnit.DAYS);
        }
        //TODO 发rabbitmq 出去
    }

    /**
     * 修改购物车的数量
     */
    @PostMapping(value = "/updateCart")
    public void updateCart(Cart obj) {
        String key = CART_KEY + obj.getUserId();
        this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
        //TODO 发rabbitmq 出去
    }

    /**
     *删除购物车
     */
    @PostMapping(value = "/delCart")
    public void delCart(Long userId, Long productId) {
        String key = CART_KEY + userId;
        this.redisTemplate.opsForHash().delete(key, productId.toString());
        //TODO 发rabbitmq 出去
    }


    @PostMapping(value = "/findAll")
    public CartPage findAll(Long userId) {
        String key = CART_KEY + userId;
        CartPage cartPage = new CartPage();
        //查购物车的总数
        long size = this.redisTemplate.opsForHash().size(key);
        cartPage.setCount((int) size);

        //查询购物车的所有商品
        //entries=hgetall命令
        Map<String, Integer> map = this.redisTemplate.opsForHash().entries(key);
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(Long.parseLong(entry.getKey()));
            cart.setAmount(entry.getValue());
            cartList.add(cart);
        }
        cartPage.setCartList(cartList);
        return cartPage;
    }
}












