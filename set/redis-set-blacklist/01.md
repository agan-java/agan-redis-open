

# 基于redis淘宝评价黑名单校验器
### 一、黑名单过滤器业务场景分析
淘宝的商品评价功能，不是任何人就能评价的，有一种职业就是差评师，差评师就是勒索敲诈商家，
这种差评师在淘宝里面就被设置了黑名单，即使购买了商品，也评价不了。


### 二、黑名单校验器的redis技术方案

黑名单过滤器除了针对上文说的淘宝评价，针对用户黑名单外，其实还有ip黑名单、设备黑名单等。  
在高并发的情况下，通过数据库过滤明显不符合要求，一般的做法都是通过Redis来实现的。  
那redis那种数据结构适合做这种黑名单的呢？   
答案是：set
步骤1：先把数据库的数据同步到redis的set集合中。  
步骤2：评价的时候验证是否为黑名单，通过sismember命令来实现。



### 三、SpringBoot+Redis 实现黑名单校验器
##### 步骤1：提前先把数据刷新到redis缓存中。
``` 

/**
 * 提前先把数据刷新到redis缓存中
 */
@PostConstruct
public void init(){
    log.info("启动初始化 ..........");
    List<Integer> blacklist=this.blacklist();
    //this.redisTemplate.delete(Constants.BLACKLIST_KEY);
    blacklist.forEach(t->this.redisTemplate.opsForSet().add(Constants.BLACKLIST_KEY,t));
}

/**
 * 模拟100个黑名单
 */
public List<Integer> blacklist() {
    List<Integer> list=new ArrayList<>();
    for (int i = 0; i < 100; i++) {
        list.add(i);
    }
    return list;
}
```

##### 步骤2：编写黑名单校验器接口
``` 

/**
 *编写黑名单校验器接口
 * true=黑名单
 * false=不是黑名单
 */
@GetMapping(value = "/isBlacklist")
public boolean isBlacklist(Integer userId) {
    boolean bo=false;
    try {
        //到set集合中去校验是否黑名单，
        bo = this.redisTemplate.opsForSet().isMember(Constants.BLACKLIST_KEY,userId);
        log.info("查询结果：{}", bo);
    } catch (Exception ex) {
        //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
        log.error("exception:", ex);
        //TODO 走DB查询
    }
    return bo;
}
```


