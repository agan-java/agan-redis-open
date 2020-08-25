
### 微博点赞实战
#### 一、微博点赞业务场景分析
梳理点赞的业务场景,它有2个接口：
第一个：点赞或取消点赞，用户点击功能
第二个接口：查看帖子信息：通过用户id 和帖子id，查看该帖子的点赞数、该用户是否点赞状态。

#### 二、微博点赞的技术方案
点赞的关键技术就是要判断该用户是否点赞，已重复点赞的不允许再点赞，即过滤重复，虽然业务不复杂，可以采用数据库直接实现，
但是对于微博这种高并发的场景，不可能查数据库的，一般是缓存，即redis
ok,我们来对上文梳理的2个接口进行技术分析：
第一个：点赞或取消点赞，用户点击功能
采用的是redis的set数据结构，key=like：postid    value={userid}
采用sadd命令，添加点赞
```
127.0.0.1:6379> sadd like:1000 101
(integer) 1
127.0.0.1:6379> sadd like:1000 102
(integer) 1
127.0.0.1:6379> sadd like:1000 103
(integer) 1
127.0.0.1:6379> smembers like:1000
1) "101"
2) "102"
3) "103" 
```
采用srem命令，取消点赞
``` 
127.0.0.1:6379> srem like:1000 101
(integer) 1
127.0.0.1:6379> smembers like:1000
1) "102"
2) "103"
```
第二个接口：查看帖子信息：通过用户id 和帖子id，查看该帖子的点赞数、该用户是否点赞状态。
采用scard命令，点赞总数
``` 
127.0.0.1:6379> smembers like:1000
1) "102"
2) "103"
127.0.0.1:6379> scard like:1000
(integer) 2
```
采用sismember命令，判断是否点赞
``` 
127.0.0.1:6379> smembers like:1000
1) "102"
2) "103"
127.0.0.1:6379> sismember like:1000 102
(integer) 1
127.0.0.1:6379> sismember like:1000 101
(integer) 0
```

#### 三、案例实战：SpringBoot+Redis 实现微博点赞

#####步骤1：点赞逻辑
``` 

    /**
     * 点赞
     */
    @GetMapping(value = "/dolike")
    public String dolike(int postid,int userid) {
        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long object = this.redisTemplate.opsForSet().add(key,userid);
            if (object==1){
                result="点赞成功";
            }else{
                result="你已重复点赞";
            }
            log.info("查询结果：{}", object);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return result;
    }

    /**
     * 取消点赞
     */
    @GetMapping(value = "/undolike")
    public String undolike(int postid,int userid) {
        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long object = this.redisTemplate.opsForSet().remove(key,userid);
            if (object==1){
                result="取消成功";
            }else{
                result="你已重复取消点赞";
            }
            log.info("查询结果：{}", object);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return result;
    }

    /**
     * 根据postid userid查看帖子信息，返回结果是点赞总数和是否点赞
     */
    @GetMapping(value = "/getpost")
    public Map getpost(int postid,int userid) {
        Map map=new HashMap();

        String result="";
        try {
            String key=Constants.LIKE_KEY+postid;
            long size = this.redisTemplate.opsForSet().size(key);
            boolean bo=this.redisTemplate.opsForSet().isMember(key,userid);
            map.put("size",size);
            map.put("isLike",bo);
            log.info("查询结果：{}", map);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return map;
    }

    /**
     * 查看点赞明细，就是有哪些人
     */
    @GetMapping(value = "/likedetail")
    public Set likedetail(int postid) {
        Set set=null;
        try {
            String key=Constants.LIKE_KEY+postid;
            set = this.redisTemplate.opsForSet().members(key);
            log.info("查询结果：{}", set);
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return set;
    }
```

#####步骤2：体验