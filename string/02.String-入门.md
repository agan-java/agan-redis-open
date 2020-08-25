# 有三种类型：字符串，整数，浮点。
例子：把java对象转换为json，然后作为字符串存储
'{"id":1,"username":"agan01","password":"123456","sex":1}'




# 客户端连接服务器
./redis-cli -h {host} -p {port} 方式连接，然后所有的操作都是在交互的方式实现




## SET
语法： SET key value [NX] [XX] [EX <seconds>] [PX [millseconds]] 设置一对key value
必选参数说明  
SET：命令
key：待设置的key
value: 设置的key的value

可选参数说明  
NX：表示key不存在才设置，如果存在则返回NULL
XX：表示key存在时才设置，如果不存在则返回NULL
EX seconds：设置过期时间，过期时间精确为秒
PX millsecond：设置过期时间，过期时间精确为毫秒

SET：命令
``` 
127.0.0.1:6379> set user1 '{"id":1,"username":"agan01","password":"123456","sex":1}'
OK
127.0.0.1:6379> get user1
"{\"id\":1,\"username\":\"agan01\",\"password\":\"123456\",\"sex\":1}"
```
NX：表示key不存在才设置，如果存在则返回NULL
``` 
127.0.0.1:6379> set user1 '{"id":1,"username":"agan01","password":"123456","sex":1}' NX
(nil)
因为user1已经存在，所以设置失败，返回nil
```

XX：表示key存在时才设置，如果不存在则返回NULL
``` 
127.0.0.1:6379> set user1 '{"id":2,"username":"agan01","password":"123456","sex":1}' XX
OK
127.0.0.1:6379> get user1
"{\"id\":2,\"username\":\"agan01\",\"password\":\"123456\",\"sex\":1}"
```

EX seconds：设置过期时间，过期时间精确为秒
采用ttl查看剩余过期时间
``` 
127.0.0.1:6379> set user1 '{"id":2,"username":"agan01","password":"123456","sex":1}' EX 600
OK
```
PX millsecond：设置过期时间，过期时间精确为毫秒
```  
127.0.0.1:6379> set user1 '{"id":2,"username":"agan01","password":"123456","sex":1}' PX 600000
OK
```

##SETNX
语法：SETNX key value
所有参数为必选参数,设置一对key value，如果key存在，则设置失败，等同于 SET key value NX
##SETEX
语法：SETEX key expire value
所有参数为必选参数，设置一对 key value，并设过期时间,单位为秒，等同于 SET key value EX expire
##PSETEX
语法：PSETEX key expire value
所有参数为必选参数，设置一对 key value，并设过期时间,单位为毫秒，等同于 SET key value PX expire



##MSET
作用：批量设值
语法：MSET key1 value1 [key2 value2 key3 value3 ...]
所有参数为必选，key、value对至少为一对。该命令功能是设置多对key-value值。
##MGET
作用：批量取值
语法：MGET key1 [key2 key3 ...]
所有参数为必选，key值至少为一个，获取多个key的value值，key值存的返回对应的value，不存在的返回NULL
``` 
127.0.0.1:6379> mset k1 v1 k2 v2 k3 v3
OK
127.0.0.1:6379> mget k1 k2 k3
1) "v1"
2) "v2"
3) "v3"
```

## GETSET
作用：先查key出value的值，然后再修改新值
语法：GETSET key value
所有参数为必选参数，获取指定key的value，并设置key的值为新值value
```
127.0.0.1:6379> getset user1 agan1
"{\"id\":2,\"username\":\"agan01\",\"password\":\"123456\",\"sex\":1}"
127.0.0.1:6379> get user1
"agan1"
```

##SETRANGE
作用：为某个key，修改偏移量offset后的值为value
语法：SETRANGE key offset value
所有参数为必选参数，设置指定key，偏移量offset后的值为value，影响范围为value的长度， offset不能小于0
``` 
127.0.0.1:6379> set user 123456
OK
127.0.0.1:6379> setrange user 2 agan
(integer) 6
127.0.0.1:6379> get user
"12agan"
```
## GETRANGE
作用：截取字符串
语法：GETRANGE key start end
所有参数为必选参数，获取指定key指定区间的value值,
start、end可以为负数，如果为负数则反向取区间
``` 
127.0.0.1:6379> get user
"12agan"
127.0.0.1:6379> getrange user 1 3
"2ag"
127.0.0.1:6379> getrange user 0 3
"12ag"
```


## APPEND
作用：字符串拼接
语法：APPEND key str
``` 
127.0.0.1:6379> set user 123
OK
127.0.0.1:6379> append user abc
(integer) 6
127.0.0.1:6379> get user
"123abc"
```
## SUBSTR
作用：字符串截取
语法：SUBSTR key str
``` 
127.0.0.1:6379> get user
"123abc"
127.0.0.1:6379> substr user 0 3
"123a"
```

#数字操作

##INCR
作用：计数器
语法： INCR key
所有参数为必选，指定key做加1操作。指定key对应的值必须为整型，否则返回错误,操作成功后返回操作后的值
```
127.0.0.1:6379> incr product01
(integer) 1
127.0.0.1:6379> get product01
"1"
127.0.0.1:6379> incr product01
(integer) 2
127.0.0.1:6379> incr product01
(integer) 3
127.0.0.1:6379> get product01
"3"
```
##DECR
语法： DECR key
所有参数为必选，指定key做减1操作。指定key对应的值必须为整型，否则返回错误,操作成功后返回操作后的值。为DECR的逆操作。
``` 
127.0.0.1:6379> get product01
"3"
127.0.0.1:6379> decr product01
(integer) 2
127.0.0.1:6379> decr product01
(integer) 1
127.0.0.1:6379> get product01
"1"
```
##INCRBY
作用：加法
语法：INCRBY key data
所有参数为必选参数，指定key做加data操作,指定key对应的值和data必须为整型，否则返回错误,操作成功后返回操作后的值
```
127.0.0.1:6379> set product01 100
OK
127.0.0.1:6379> get product01
"100"
127.0.0.1:6379> incrby product01 20
(integer) 120
127.0.0.1:6379> get product01
"120"
```
## DECRBY
作用：减法
语法：DECRBY key data
所有参数为必选参数，指定key做减data操作,指定key对应的值和data必须为整型，否则返回错误,操作成功后返回操作后的值
```
127.0.0.1:6379> get product01
"120"
127.0.0.1:6379> decrby product01 30
(integer) 90
127.0.0.1:6379> get product01
"90"
```
### INCRBYFLOUT KEY NUM
在原有的key上加上浮点数
``` 
127.0.0.1:6379> set money 10.5
OK
127.0.0.1:6379> incrbyfloat money 2.2
"12.7"
```










