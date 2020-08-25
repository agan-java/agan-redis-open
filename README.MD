### 一：redis分布式缓存(String专题)  
redis安装部署  
    1.为什么要用redis，它解决了什么问题？  
    2.linux安装部署redis 5.x  
    3.String数据结构的字符串、整数、浮点操作  
SpringBoot集成Redis  
    1.案例实战：SpringBoot + Mybatis + Redis的缓存实战  
    2.案例实战：优化重写Redis的序列化  
SpringCache集成Redis  
    1.为什么要用SpringCache,它解决了什么问题？  
    2.案例实战：SpringCache+SpringBoot + Mybatis  自动实现redis缓存  
    3.剖析SpringCache常用注解  
    4.总结springcache在开发中遇到的大坑小坑  
微信文章的阅读量  
    1.微信文章的阅读量PV场景介绍  
    2.高并发的微信文章的阅读量PV技术原理  
    3.案例实战：SpringBoot+Redis实现微信文章的PV统计  
淘宝商品分布式全局id  
    1. 为什么分布式系统需要全局唯一id ?  
    2. 全局唯一id必须具备什么特点 ？  
    3. 剖析淘宝商品id的特点  
    4. 基于Redis生成分布式全局唯一id原理  
    5. 案例实战：SpringBoot+redis生成淘宝商品id  
redis性能优化实战  
    1.真实案例分析Redis的性能瓶颈  
    2.什么是lua ?  
    3.Redis 为什么要使用LUA ？  
    4.lua的语法入门  
    5.真实案例分析Redis的性能瓶颈及如何优化？  
    6.案例实战：Springoot实现多条redis命令合成一个lua  
    7.案例实战：Redis+lua 实现黑客防刷攻击  
    8.案例实战：SpringBoot+Redis+lua 实现黑客防刷攻击  
        
### 二：redis分布式缓存(Hash专题)  
 Redis经典场景：hash存储java对象  
    1.什么是Redis的hash数据结构？  
    2.Redis的hash结构经典场景：存储java对象  
    3.Redis存储java对象，什么时候用String 什么时候用Hash  
    4.案例实战：SpringBoot+Redis+Hash存储商品数据  
淘宝短链接实战  
    1.体验淘宝短链接业务场景  
    2.把URL生成短链接的算法  
    3.案例实战：SpringBoot+Redis高并发《短链接转换器》  
京东双11购物车实战  
    1.京东购物车多种场景复杂分析  
    2.图解分析：双11高并发的京东购物车技术方案  
    3.购物车的redis经典场景  
    4.案例实战：SpringBoot+Redis 实现高并发的购物车  
    5.案例实战：SpringBoot+Redis+Cookies实现高并发的购物车  
Redis解决分布式系统的session不一致性问题  
    1.Session有什么作用？  
    2.大厂面试题：分布式Session有什么问题？  
    3.案例实战：SpringBoot实现用户登录Session实战  
    4.采用Docker安装部署Nginx  
    5.采用Docker部署Nginx的集群负载均衡  
    6.剖析SpringBoot+Nginx的分布式Session不一致性问题  
    7.案例实战：采用Redis解决分布式系统的Session不一致性问题  
    8.剖析SpringSession的Redis原理  
微博实战：用户注册  
    1.用户注册微博的redis技术方案  
    2.案例实战：SpringBoot+Redis 实现微博注册  
微博实战：用户发微博  
    1.用户发微博业务场景分析  
    2.用户发微博的Redis技术方案  
    3.案例实战：SpringBoot+Redis 实现用户发微博  
    
### 三：redis分布式缓存设计(Set专题)  
set数据结构详解  
    set的常用命令实战  
淘宝商品评价黑名单校验器  
    1.淘宝黑名单校验器业务场景分析  
    2.淘宝黑名单校验器的redis技术方案  
    3.SpringBoot+Redis 实现黑名单校验器  
京东京豆抽奖实战  
    1.京东京豆抽奖的业务场景分析  
    2.京东京豆抽奖的技术方案  
    3.案例实战：SpringBoot+Redis 实现京东京豆抽奖  
支付宝抽奖实战  
    1.支付宝抽奖的业务场景分析  
    2.支付宝抽奖的技术方案  
    3.案例实战：SpringBoot+Redis 实现支付宝抽奖  
微博好友、榜单、QQ群随机展示  
    1.微博好友、榜单、QQ群随机展示业务场景分析  
    2.随机展示的redis技术方案  
    3.SpringBoot+Redis 微博好友、QQ群 随机推荐  
    4.SpringBoot+Redis 微博榜单随机推荐  
微博实战：帖子点赞  
    1.微博点赞业务场景分析  
    2.微博点赞的技术方案  
    3.案例实战：SpringBoot+Redis 实现微博点赞  
微博实战：微博关注与粉丝  
    1.微博关注与粉丝的业务场景分析  
    2.微博关注与粉丝的redis技术方案  
    3.SpringBoot+Redis 实现微博关注与粉丝  
微博实战：微关系计算  
    1.计算好友关系业务场景分析  
    2.计算好友关系的redis技术方案  
    3.SpringBoot+Redis 计算微博好友关系  
淘宝亿级的UV计算  
    1.淘宝首页亿级UV的技术方案分析  
    2.HyperLogLog 详解  
    3.案例实战:基于Redis的UV计算  
    4.HyperLogLog设计原理：伯努利试验  
    5.HyperLogLog的内存空间设计  
    6.剖析HyperLogLog的pfadd存储的设计原理  
    7.剖析HyperLogLog的pfcount统计设计原理  
    8.HyperLogLog更精准的概率优化  
    
### 四：redis分布式缓存设计(List专题)   
List命令实战  
    1.什么是redis的list数据结构？  
    2.Redis list命令实战  
高并发淘宝聚划算商品列表  
    1.需求分析:淘宝聚划算功能  
    2.高并发的淘宝聚划算实现技术方案  
    3.案例实战:SpringBoot+Redis实现淘宝聚划算功能  
淘宝聚划算商品列表：缓存击穿  
    1.什么是缓存击穿？  
    2.如何解决缓存击穿的问题？  
    3.淘宝聚划算的缓存击穿实现  
高并发的微信抢红包  
    1.微信抢红包的并发场景分析  
    2.微信抢红包的技术实现原理  
    3.案例实战:SpringBoot+Redis实现微信抢红包  
高并发微信文章PV  
    1.高并发微信文章的阅读量PV业务场景分析  
    2.基于二级缓存的高并发微信文章的阅读量PV技术方案  
    3.案例实战：SpringBoot+Redis实现二级缓存微信文章的PV统计  
微博实战：push推送  
    1.微博push推送业务场景分析  
    2.微博push推送的redis技术方案  
    3.案例实战:基于push技术，实现微博个人列表  
    4.案例实战:基于push技术，实现微博关注列表  
微博实战：push列表的性能优化  
    1.微博个人和关注列表的性能瓶颈分析  
    2.微博个人和关注列表的性能优化方案   
    3.案例实战:微博个人和关注列表的性能优化  
Redis分布式普通锁  
    1.为什么要使用分布式锁，它解决了什么问题？  
    2.分布式锁必须具备5个特性  
    3.业界有哪些主流的分布式锁实现方案？  
    4.基于Redis的SETNX实现分布式锁   
    5.案例实战：基于redis的分布式锁实现下订单防止重复提交    
Redis分布式重入锁  
    1.大厂面试题：什么是不可重入锁？  
    2..案例实战：设计一个不可重入锁  
    3.大厂面试题：什么是可重入锁？它有什么作用？  
    4.案例实战：基于synchronized下订单的可重入锁  
    5.案例实战：基于ReentrantLock的递归锁  
    6.redis如何实现分布式重入锁？  
    7.什么是 Redisson？  
    8.案例实战：体验redis分布式重入锁  
Redis分布式红锁  
    1.图解分析：基于setnx的分布式锁有什么缺陷？  
    2.redlock算法的设计原理  
    3.案例实战:部署3台Redis分布式红锁实例  
    4.案例实战：分布式红锁  
剖析Redisson分布式redlock源码  
    1.分布式红锁的waitTime的设计原理  
    2.分布式红锁的加锁的lua底层设计原理  
    3.分布式红锁的leaseTime的设计原理  
    4.分布式红锁的加锁失败的设计原理  
    


### 五：redis分布式缓存设计(zset专题)  
zset命令实战  
    什么是redis zset类型？  
    zset命令实战  
微博实战：微博热搜排行榜  
    1. 微博热度排行榜业务场景分析  
    2. 微博热度排行榜的redis技术方案  
    3. springboot+redis实现微博热搜排行榜  
美团地图附近酒店搜索  
    1.美团附近酒店搜索-业务场景分析  
    2.美团附近酒店搜索-技术方案  
    3.SpringBoot+Redis实现美团附近酒店搜索  
微博实战: 关注pull推送  
    1.微博push和pull有什么区别？  
    2.微博pull拉取的redis技术方案  
    3.案例实战:基于pull技术，实现微博个人列表  
    4.案例实战:基于pull技术，实现微博关注列表  
淘宝直播卖货  
    1.淘宝直播卖货的业务场景分析  
    2.淘宝直播卖货的弹幕技术方案  
    3.案例实战：基于redis的直播发言弹幕  
今日头条推荐引擎-布隆过滤器  
    1.推荐引擎已读去重-业务场景分析  
    2.如何实现已读去重？  
    3.什么是布隆过滤器？  
    4.采用docker安装RedisBloom  
    5.SpringBoot+Redis 推荐引擎已读去重  
    6.布隆过滤器设计原理解剖  
redis的IM的聊天工具  
    1.什么是redis的stream数据结构？  
    2.stream生产消息  
    3.stream独立消费  
    4.消息ID的原理  
    5.redis的IM聊天工具  
    6.Springboot实现redis的IM聊天工具  
微服务的stream消息队列  
    1.为什么要用消息组？它解决了什么问题？  
    2.案例实战：积分服务消息组  
    3.如何确保消息100%消费成功？  
    4.没人消费的消息，采用消息转移  
    5.删除死信消息  
    6.案例实战：积分服务的消费队列   
    7.案例实战：积分服务+push服务的集群消费队列  
    
### 六：Spring源码分析：Spring与Redis网络通信原理剖析
一、redis哈希槽分区  
    1.图解分析：哈希取余分区原理  
    2.图解分析：一致性哈希分区原理  
    3.图解分析：哈希槽分区原理  
    4.哈希槽案例实战：Redis集群哈希槽部署  
    5.哈希槽案例实战：查看Redis哈希槽的数据分布  
    6.哈希槽案例实战：redis集群哈希槽扩容演练  
    7.哈希槽案例实战：redis集群哈希槽收缩演练  
    8.哈希槽案例实战：spring集成redis集群哈希槽演练  
二、Spring与Redis通信的设计结构  
    1.Spring与Redis通信的设计结构  
    2. 什么是Lettuce？  
    3. 基于Lettuce单机连接Redis   
    4. 基于Lettuce集群连接Redis  
三、Redis通信协议  
    1.大厂面试题：请说下redis通讯协议RESP ？  
    2.源码剖析：Lettuce《redis通讯协议RESP》的设计原理   
    3.图解分析：Lettuce《redis通讯协议RESP》的设计原理   
四、Redis拓扑结构图  
    1.源码剖析：Lettuce如何读取Redis的集群拓扑结构图？  
    2.图解分析：Lettuce如何读取Redis的集群拓扑结构图？  
    3.大厂面试题：Redis集群3主3从，spring配置文件该配几个ip？配1个还是6个？  
    4.大厂面试题：spring如何知道redis每个节点有多少个槽？  
    5.大厂面试题：spring如何实现redis槽和节点的映射关系？  
    6.大厂面试题：spring如何计算redis哈希槽？  
    7.大厂面试题：有了哈希号，spring如何算出Redis集群节点IP？  
五、Spring和Redis网络通信原理  
    1.网络通信基础：netty入门  
    2.网络通信基础：netty的设计原理之TCP连接  
    3.网络通信基础：netty的设计原理之数据发送与接收  
    4.网络通信基础：netty的设计原理之Pipeline链表  
    5.网络通信基础：netty通信NIO异步变同步设计方案  
    6.网络通信基础：netty通信NIO异步变同步实战演练  
    7.异步编程基础：CompletionStage异步编程-入门  
    8.异步编程基础：CompletionStage异步编程-进阶  
    9.异步编程基础：CompletionStage异步编程-高级  
    10.源码剖析：Lettuce如何tcp连接Redis（一）  
    11.对象池基础：为什么需要对象池？  
    12.对象池基础：对象池入门例子  
    13.对象池基础：图解分析对象池设计原理  
    14.源码剖析：Spring如何设计封装Redis连接池?  
    15.实战演练：模仿spring封装Redis连接池，自己手写一个  
    16.反应式编程基础：什么是反应式编程？  
    17.反应式编程基础：Spring的反应式编程  
    18.反应式编程基础：反应式编程Reactor入门例子  
    19.lettuce如何tcp连接redis（二）  
    20.spring每次请求redis是用1条tcp还是多条tcp？  
    21.基础知识点：动态代理案例  
    22.当lettuce异步发送redis，如何把数据等回来？  
    23.lettuce如何给redis发消息？  
    24.当lettuce异步收到redis消息，如何发送线程绑定在一起？  
    25.模仿lettuce异步发送接收redis的设计原理，自己手写一个  
六、Redis通信异常处理  
    1.Redis TCP 连接异常断开后，如何重连？  
    2.发送者Endpoint 和 CommandHandler的设计原理  
    3.redis断开重连，新channel如何加入发送者Endpoint？  
    4.redis的tcp异常断开后，未发送的数据如何存储？  
    5.如何把tcp异常断开前的数据，再次发送出去？  
    6.什么是redis 集群MOVED异常？  
    7.什么是redis 集群ASK异常？  
    8.当redis集群主从切换、哈希槽迁移，spring如何知道？  