### 什么是redis的list数据结构？
List类型是一个双端链表的结构，容量是2的32次方减1个元素，即40多亿个；  
其主要功能有push、pop、获取元素等；一般应用在栈、队列、消息队列等场景。

### Redis list命令实战
### [LR]PUSH key value1 [value2 ...]
以头插或尾插方式插入指定key队列中一个或多个元素
### LRANGE key start stop
获取列表指定范围内的元素
``` 
127.0.0.1:6379> lpush products 1 2 3
(integer) 3
127.0.0.1:6379> lpush products 4 5 6
(integer) 6
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "4"
4) "3"
5) "2"
6) "1"
```

### LINSERT key BEFORE|AFTER pivot value
在列表的元素前或者后插入元素
``` 
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "4"
4) "3"
5) "2"
6) "1"
127.0.0.1:6379> linsert products before 4 a
(integer) 7
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "a"
4) "4"
5) "3"
6) "2"
7) "1"
127.0.0.1:6379> linsert products after 4 b
(integer) 8
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "a"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
```

### LLEN key
获取列表长度
``` 
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "a"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
127.0.0.1:6379> llen products
(integer) 8
```

### LINDEX key index
通过索引获取列表中的元素
``` 
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "a"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
127.0.0.1:6379> lindex products 2
"a"
```
## LSET key index value
通过索引设置列表元素的值
``` 
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "a"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
127.0.0.1:6379> lset products 2 A
OK
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "A"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
```

### LTRIM key start end
截取队列指定区间的元素,其余元素都删除
``` 
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "A"
4) "4"
5) "b"
6) "3"
7) "2"
8) "1"
127.0.0.1:6379> ltrim products 0 3
OK
127.0.0.1:6379> lrange products 0 -1
1) "6"
2) "5"
3) "A"
4) "4"
```
### LREM key count value
移除列表元素
``` 
127.0.0.1:6379> lpush test a 1 a 2 a 3 a 4  5 6
(integer) 10
127.0.0.1:6379> lrange  test 0 -1
 1) "6"
 2) "5"
 3) "4"
 4) "a"
 5) "3"
 6) "a"
 7) "2"
 8) "a"
 9) "1"
10) "a"
127.0.0.1:6379> lrem test 4 a
(integer) 4
127.0.0.1:6379> lrange  test 0 -1
1) "6"
2) "5"
3) "4"
4) "3"
5) "2"
6) "1"
```

### [LR]POP key
从队列的头或未弹出节点元素(返回该元素并从队列中删除)
``` 
127.0.0.1:6379> lrange  test 0 -1
1) "6"
2) "5"
3) "4"
4) "3"
5) "2"
6) "1"
127.0.0.1:6379> lpop test
"6"
127.0.0.1:6379> lrange  test 0 -1
1) "5"
2) "4"
3) "3"
4) "2"
5) "1"
127.0.0.1:6379> lpop test
"5"
127.0.0.1:6379> lrange  test 0 -1
1) "4"
2) "3"
3) "2"
4) "1"
```

### RPOPLPUSH source destination
移除列表的最后一个元素，并将该元素添加到另一个列表并返回
``` 
127.0.0.1:6379> lrange src 0 -1
1) "3"
2) "2"
3) "1"
127.0.0.1:6379> lrange dst 0 -1
1) "c"
2) "b"
3) "a"
127.0.0.1:6379> rpoplpush src dst
"1"
127.0.0.1:6379> lrange src 0 -1
1) "3"
2) "2"
127.0.0.1:6379> lrange dst 0 -1
1) "1"
2) "c"
3) "b"
4) "a"
```
## B[LR]POP key1 [key2 ...] timeout
移出并获取列表的第一个或最后一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
``` 
127.0.0.1:6379> lpush list1 1 2
(integer) 2
127.0.0.1:6379> lpush list2 a b
(integer) 2
127.0.0.1:6379> lrange list1 0 -1
1) "2"
2) "1"
127.0.0.1:6379> lrange list2 0 -1
1) "b"
2) "a"
127.0.0.1:6379> blpop list1 list2 10
1) "list1"   #弹出元素所属的列表
2) "2"       #弹出元素所属的值
127.0.0.1:6379> blpop list1 list2 10
1) "list1"
2) "1"
127.0.0.1:6379> blpop list1 list2 10
1) "list2"
2) "b"
127.0.0.1:6379> blpop list1 list2 10
1) "list2"
2) "a"
127.0.0.1:6379> blpop list1 list2 10
(nil)
(10.08s)  # 列表为空的时候，就等待超时 
```


