### 为什么要用redis，它解决了什么问题？
Redis 是一个高性能的key-value内存数据库。它支持常用的5种数据结构：String字符串、Hash哈希表、List列表、Set集合、Zset有序集合 等数据类型。  
Redis它解决了2个问题：  
**第一个是：性能**  
通常数据库的读操作，一般都要几十毫秒，而redisd的读操作一般仅需不到1毫秒。通常只要把数据库的数据缓存进redis，就能得到几十倍甚至上百倍的性能提升。  
**第二个是：并发**  
在大并发的情况下，所有的请求直接访问数据库，数据库会出现连接异常，甚至卡死在数据库中。为了解决大并发卡死的问题，一般的做法是采用redis做一个缓冲操作，让请求先访问到redis，而不是直接访问数据库。



### linux安装部署redis 5.x
#### 步骤一：打开官网下载
https://redis.io/download

#### 步骤二：安装
下载，编译Redis
``` 
$ wget http://download.redis.io/releases/redis-5.0.7.tar.gz
$ tar xzf redis-5.0.7.tar.gz
$ cd redis-5.0.7
$ make
```

启动
``` 
./src/redis-server
```
#### 步骤三：测试体验 客户端连接服务器
./redis-cli -h {host} -p {port} 方式连接，然后所有的操作都是在交互的方式实现
``` 
[root@node2 redis-5.0.7]# ./src/redis-cli
127.0.0.1:6379> set user1 agan
OK
127.0.0.1:6379> get user1
"agan"
127.0.0.1:6379>
```

#### 步骤四：以后台进程方式启动redis
为什么要以后台的方式启动redis?
因为 ./redis-server 以这种方式启动redis，需要一直打开窗口，不能进行其他操作，不太方便。  
按 ctrl + c可以关闭窗口。  

第一步：修改redis.conf文件
将
```
daemonize no
```
修改为
```
daemonize yes
```
第二步：指定redis.conf文件启动
``` 
[root@node2 redis-5.0.7]# ./src/redis-server  /data/redis-5.0.7/redis.conf
9937:C 21 Dec 2019 16:43:19.724 # oO0OoO0OoO0Oo Redis is starting oO0OoO0OoO0Oo
9937:C 21 Dec 2019 16:43:19.724 # Redis version=5.0.7, bits=64, commit=00000000, modified=0, pid=9937, just started
9937:C 21 Dec 2019 16:43:19.724 # Configuration loaded
```

第三步：测试体验
``` 
[root@node2 redis-5.0.7]# ./src/redis-cli
127.0.0.1:6379> set user1 agan
OK
127.0.0.1:6379> get user1
"agan"
127.0.0.1:6379>
```

