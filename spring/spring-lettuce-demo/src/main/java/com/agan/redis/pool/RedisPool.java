package com.agan.redis.pool;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
public class RedisPool {

    public static void main(String[] args) throws Exception {
        test();
    }

    public static void  test() throws Exception {
        //步骤1：创建对象池的配置信息
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        // 最大空闲数
        poolConfig.setMaxIdle(5);
        // 最小空闲数, 池中只有一个空闲对象的时候，池会在创建一个对象，并借出一个对象，从而保证池中最小空闲数为1
        poolConfig.setMinIdle(1);
        // 最大池对象总数
        poolConfig.setMaxTotal(20);

        //步骤2：RedisURI：连接信息。
        RedisURI redisUri = RedisURI.builder()
                .withHost("39.100.196.99")
                .withPort(6379)
                .withPassword("agan")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        //步骤3：RedisClient：Redis客户端，
        RedisClient redisClient = RedisClient.create(redisUri);

        //步骤4：创建ObjectPool（对象池）
        GenericObjectPool<StatefulRedisConnection<String, String>> pool =
                ConnectionPoolSupport.createGenericObjectPool(
                        //步骤5：Connection：Redis连接
                () -> redisClient.connect(), poolConfig, false);

        for (int i = 0; i < 10; i++) {
            //步骤6：借redis连接
            StatefulRedisConnection<String, String> connection = pool.borrowObject();
            RedisCommands<String,String> commands = connection.sync();
            String key="key"+i;
            commands.set(key,"hello world"+i);
            String str = commands.get(key);
            log.debug("--------------{}-------value-------{}-----------",connection,str);

            ////步骤7：归还，放回池中的话，每次都是同一个对象
            pool.returnObject(connection);
        }

        pool.close();
        redisClient.shutdown();
    }
}
