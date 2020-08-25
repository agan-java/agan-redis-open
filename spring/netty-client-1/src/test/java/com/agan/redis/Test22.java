package com.agan.redis;

import com.agan.redis.common.DateUtil;
import io.lettuce.core.cluster.SlotHash;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test22 {
    public static void main(String[] args) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for(int i=0;i<24;i++){
            try {
                String temp="2020-3-31 "+i+":00";
                Date date = simpleDateFormat.parse(temp);
                System.out.println(temp+"   "+date.getTime()/(1000*60*60*24));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
