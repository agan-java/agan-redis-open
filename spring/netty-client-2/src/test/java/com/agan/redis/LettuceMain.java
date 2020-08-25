package com.agan.redis;

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
public class LettuceMain {

    public static void main(String[] args) throws Exception {
        RedisURI redisUri = RedisURI.builder()
                .withHost("39.100.196.99")
                .withPort(6379)
                .withPassword("agan")
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        GenericObjectPool<StatefulRedisConnection<String, String>> pool
                = ConnectionPoolSupport.createGenericObjectPool(redisClient::connect, poolConfig);
        try (StatefulRedisConnection<String, String> connection = pool.borrowObject()) {
            RedisCommands<String, String> command = connection.sync();
            SetArgs setArgs = SetArgs.Builder.nx().ex(5);
            command.set("name", "throwable", setArgs);
            String n = command.get("name");
            log.info("Get value:{}", n);
        }
        pool.close();
        redisClient.shutdown();
    }
}
