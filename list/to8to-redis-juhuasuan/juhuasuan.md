
### 一、需求分析:淘宝聚划算功能
https://ju.taobao.com/
这张页面的特点：
1.数据量少，才13页 
2.高并发，请求量大。

### 二、高并发的淘宝聚划算实现技术方案
像聚划算这种高并发的功能，绝对不可能用数据库的！
一般的做法是先把数据库中的数据抽取到redis里面。采用定时器，来定时缓存。
这张页面的特点，数据量不多，才13页。最大的特点就要支持分页。
redisde list数据结构天然支持这种高并发的分页查询功能。

具体的技术方案采用list 的lpush 和 lrange来实现。
``` 
## 先用定时器把数据刷新到list中
127.0.0.1:6379> lpush jhs p1 p2 p3 p4 p5 p6 p7 p8 p9 p10
(integer) 10
## 用lrange来实现分页
127.0.0.1:6379> lrange jhs 0 5
1) "p10"
2) "p9"
3) "p8"
4) "p7"
5) "p6"
6) "p5"
127.0.0.1:6379> lrange jhs 6 10
1) "p4"
2) "p3"
3) "p2"
4) "p1"
```



### 三、案例实战：SpringBoot+Redis实现淘宝聚划算功能
#### 步骤0：配置redis

#### 步骤1：采用定时器把特价商品都刷入redis缓存中
``` 
@Service
@Slf4j
public class TaskService {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void initJHS(){
        log.info("启动定时器..........");
        new Thread(()->runJhs()).start();
    }


    /**
     * 模拟定时器，定时把数据库的特价商品，刷新到redis中
     */
    public void runJhs() {
        while (true){
            //模拟从数据库读取100件特价商品，用于加载到聚划算的页面中
            List<Product> list=this.products();
            //采用redis list数据结构的lpush来实现存储
            this.redisTemplate.delete(Constants.JHS_KEY);
            //lpush命令
            this.redisTemplate.opsForList().leftPushAll(Constants.JHS_KEY,list);
            try {
                //间隔一分钟 执行一遍
                Thread.sleep(1000*60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("runJhs定时刷新..............");
        }
    }


    /**
     * 模拟从数据库读取100件特价商品，用于加载到聚划算的页面中
     */
    public List<Product> products() {
        List<Product> list=new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Random rand = new Random();
            int id= rand.nextInt(10000);
            Product obj=new Product((long) id,"product"+i,i,"detail");
            list.add(obj);
        }
        return list;
    }
}
```
#### 步骤2：redis分页查询
``` 
    /**
     * 分页查询：在高并发的情况下，只能走redis查询，走db的话必定会把db打垮
     */
    @GetMapping(value = "/find")
    public List<Product> find(int page, int size) {
        List<Product> list=null;
        long start = (page - 1) * size;
        long end = start + size - 1;
        try {
            //采用redis list数据结构的lrange命令实现分页查询
            list = this.redisTemplate.opsForList().range(Constants.JHS_KEY, start, end);
            if (CollectionUtils.isEmpty(list)) {
                //TODO 走DB查询
            }
            log.info("查询结果：{}", list);
        } catch (Exception ex) {
            //这里的异常，一般是redis瘫痪 ，或 redis网络timeout
            log.error("exception:", ex);
            //TODO 走DB查询
        }
        return list;
    }

```




