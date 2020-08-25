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

@Slf4j
public class LettucecCluster {
    public static void main(String[] args) {
        operCluster();
    }

    public static void operCluster(){
        //步骤1：RedisURI：连接信息。
        List<RedisURI> list = new ArrayList<>();
        list.add(RedisURI.create("redis://39.100.196.99:6381"));
        list.add(RedisURI.create("redis://39.100.196.99:6382"));
        list.add(RedisURI.create("redis://39.100.196.99:6383"));
        list.add(RedisURI.create("redis://39.100.196.99:6384"));
        list.add(RedisURI.create("redis://39.100.196.99:6385"));
        list.add(RedisURI.create("redis://39.100.196.99:6386"));
        //步骤2：RedisClusterClient：Redis集群客户端
        RedisClusterClient client = RedisClusterClient.create(list);
        //步骤3：Connection：Redis连接 （集群）
        StatefulRedisClusterConnection<String, String> connect = client.connect();

        //步骤4：RedisCommands：Redis命令API接口
        /**
         * sync同步调用
         */
        RedisAdvancedClusterCommands<String, String> commands = connect.sync();
        commands.set("hello","hello world");
        String str = commands.get("hello");
        log.debug("-------同步-------{}-----------",str);

        /**
         * async异步调用
         */
        RedisAdvancedClusterAsyncCommands<String,String> asyncCommands = connect.async();
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

