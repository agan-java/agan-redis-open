package com.agan.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class TestMap {
    public static void main(String[] args) {
        String key="a";
        Integer n=100;
        // 一般这样写
        Map<String,Integer> map=new HashMap<>();
        if(map.get(key)==null){
            map.put(key,n);
        }
        System.out.println(map);
        map.clear();

        // 简单赋值，这样写
        map.putIfAbsent(key,n);
        System.out.println(map);
        map.clear();

        //复杂操作这么写
        map.computeIfAbsent(key,k->{
            return new Integer(n+10);
        });
        System.out.println(map);
    }
}
