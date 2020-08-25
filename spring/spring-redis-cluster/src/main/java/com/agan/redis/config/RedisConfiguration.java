package com.agan.redis.config;

import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.resource.ClientResources;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;


@Configuration
public class RedisConfiguration {

//    @Value("${spring.redis.cluster.nodes}")
//    private String clusterNodes;
    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到数据库的。RedisTemplate默认使用的是JdkSerializationRedisSerializer，StringRedisTemplate默认使用的是StringRedisSerializer。
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、OxmSerializer、StringRedisSerializer。
     * 在此我们将自己配置RedisTemplate并定义Serializer。
     * redisTemplate
     *
     *
     * @param redisConnectionFactory
     * @return
     */

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

//
//    @Bean(name="clusterRedisUri")
//    RedisURI clusterRedisUri(){
//        RedisURI.Builder builder= RedisURI.builder();
//        String[] nodes = clusterNodes.split(",");
//        for(String node : nodes){
//            String[] host =  node.split(":");
//            builder.withHost(host[0]).withPort(Integer.parseInt(host[1]));
//        }
//        return builder.build();
//    }
//
//    /**
//     * 配置集群选项,自动重连,最多重定型1次
//     */
//    @Bean
//    ClusterClientOptions clusterClientOptions(){
//                ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                //开启自适应刷新
//                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
//                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
//                //开启定时刷新
//                .enablePeriodicRefresh(Duration.ofSeconds(15))
//                .build();
//                return ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build();
//    }
//
//    /**
//     * 创建集群客户端
//     */
//    @Bean
//    RedisClusterClient redisClusterClient(ClientResources clientResources, ClusterClientOptions options, RedisURI clusterRedisUri){
//        RedisClusterClient redisClusterClient = RedisClusterClient.create(clientResources,clusterRedisUri);
//        redisClusterClient.setOptions(options);
//        return redisClusterClient;
//    }
//
//    /**
//     * 集群连接
//     */
//    @Bean(destroyMethod = "close")
//    StatefulRedisClusterConnection<String,String> statefulRedisClusterConnection(RedisClusterClient redisClusterClient){
//        return redisClusterClient.connect();
//    }
}

//    /**
//     * 配置LettuceClientConfiguration 包括线程池配置和安全项配置
//     *
//     * @param genericObjectPoolConfig common-pool2线程池
//     * @return lettuceClientConfiguration
//     */
//    private LettuceClientConfiguration getLettuceClientConfiguration(GenericObjectPoolConfig genericObjectPoolConfig) {
//        /*
//        【重要！！】
//        【重要！！】
//        【重要！！】
//        ClusterTopologyRefreshOptions配置用于开启自适应刷新和定时刷新。如自适应刷新不开启，Redis集群变更时将会导致连接异常！
//         */
//        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                //开启自适应刷新
//                .enableAdaptiveRefreshTrigger(ClusterTopologyRefreshOptions.RefreshTrigger.MOVED_REDIRECT, ClusterTopologyRefreshOptions.RefreshTrigger.PERSISTENT_RECONNECTS)
//                .adaptiveRefreshTriggersTimeout(Duration.ofSeconds(10))
//                //开启定时刷新
//                .enablePeriodicRefresh(Duration.ofSeconds(15))
//                .build();
//        return LettucePoolingClientConfiguration.builder()
//                .poolConfig(genericObjectPoolConfig)
//                .clientOptions(ClusterClientOptions.builder().topologyRefreshOptions(topologyRefreshOptions).build())
//                //将appID传入连接，方便Redis监控中查看
//                .clientName(appName + "_lettuce")
//                .build();
//    }



