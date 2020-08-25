
# 基于Redis的高并发随机展示
### 随机展示业务场景分析
思考题：为什么要随机展示？
因为展示的区域有限啊，在那么小的地方展示全部数据是不可能的，通常的做法就是随机展示一批数据，然后用户点击“换一换”按钮，再随机展示另一批。


### 随机展示的redis技术方案
上文已经说了随机展示的原因就是区域有限，而区域有限的地方通常就是首页或频道页，这些位置通常都是访问量并发量非常高的，
一般是不可能采用数据库来实现的，通常都是Redis来实现。
redis的实现技术方案:
步骤1：先把数据准备好，把所有需要展示的内容存入redis的Set数据结构中。
步骤2：通过srandmember命令随机拿一批数据出来。







### SpringBoot+Redis 实现高并发随机展示

#### SpringBoot+Redis 实现好友\QQ群 随机推荐
##### 步骤1：提前先把数据刷新到redis缓存中。
``` 
    /**
     *提前先把数据刷新到redis缓存中。
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化 群..........");
        List<String> crowds=this.crowd();
        this.redisTemplate.delete(Constants.CROWD_KEY);
        crowds.forEach(t->this.redisTemplate.opsForSet().add(Constants.CROWD_KEY,t));
    }

    /**
     * 模拟100个热门群，用于推荐
     */
    public List<String> crowd() {
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Random rand = new Random();
            int id= rand.nextInt(10000);
            list.add("群"+id);
        }
        return list;
    }
```
##### 步骤2：编写随机查询接口
``` 
@GetMapping(value = "/crowd")
public List<String> crowd() {
    List<String> list=null;
    try {
        //采用redis set数据结构，随机取出10条数据
        list = this.redisTemplate.opsForSet().randomMembers(Constants.CROWD_KEY,10);
        log.info("查询结果：{}", list);
    } catch (Exception ex) {
        //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
        log.error("exception:", ex);
        //TODO 走DB查询
    }
    return list;
}
```

##### 步骤3：体验：




#### SpringBoot+Redis 微博榜单随机推荐
##### 步骤1：提前先把数据刷新到redis缓存中。
微博榜单和QQ群的区别是:微博榜单是整块数据的，所以随机的数据要按块来推荐
所以我们要定义一个java bean来包装整块数据
``` 
@Data
public class WeiboList {
    
    private int id;
    /**
     * 榜单名称
     */
    private String name;

    private List<String> users;

}
```
``` 

    /**
     * 模拟10个热门榜单，用于推荐
     */
    public List<WeiboList> list() {
        List<WeiboList> list=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            WeiboList wl=new WeiboList();
            wl.setId(i);
            wl.setName("榜单"+i);
            Random rand = new Random();
            List<String> users=new ArrayList<>();
            for (int j=0;j<3;j++){
                int id= rand.nextInt(10000);
                users.add("user:"+id);
            }
            wl.setUsers(users);
            list.add(wl);
        }
        return list;
    }
```


##### 步骤2：编写随机查询接口
``` 
    @GetMapping(value = "/weibolist")
    public WeiboList weibolist() {
        WeiboList list=null;
        try {
            //随机取1块数据
            list = (WeiboList)this.redisTemplate.opsForSet().randomMember(Constants.WEIBO_LIST_KEY);
            log.info("查询结果：{}", list);
        } catch (Exception ex) {
            //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
            log.error("exception:", ex);
            //TODO 走DB查询
        }
        return list;
    }
```

##### 步骤3：体验：
