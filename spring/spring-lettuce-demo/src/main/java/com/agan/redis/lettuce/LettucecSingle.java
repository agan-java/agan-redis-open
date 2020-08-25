package com.agan.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisAdvancedClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisAdvancedClusterCommands;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 基于Lettuce单机连接Redis
 */
@Slf4j
public class LettucecSingle {
    public static void main(String[] args) {
        operSingle();
    }

    public static void operSingle(){
        //步骤1：RedisURI：连接信息。
        RedisURI redisUri = RedisURI.builder()
                .withHost("39.100.196.99")
                .withPort(6379)
                .withPassword("agan")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        //步骤2：RedisClient：Redis客户端
        RedisClient client = RedisClient.create(redisUri);

        //步骤3：Connection：Redis连接 （单机）
        StatefulRedisConnection<String,String> connect = client.connect();

        //步骤4：RedisCommands：Redis命令API接口
        /**
         * sync同步调用
         */
        RedisCommands<String,String> commands = connect.sync();
        commands.set("hello","hello world");
        String str = commands.get("hello");
        log.debug("-------同步-------{}-----------",str);

        /**
         * async异步调用
         *
         */
        RedisAsyncCommands<String,String> asyncCommands = connect.async();
        RedisFuture<String> future = asyncCommands.get("hello");
        try {
            String str1 = future.get();
            log.debug("-------异步-------{}-----------",str1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        connect.close();
        client.shutdown();
    }

}

