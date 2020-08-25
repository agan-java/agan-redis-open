
# 二级缓存的高并发微信文章的阅读量PV
### 高并发微信文章的阅读量PV业务场景分析

### 二级缓存的高并发微信文章的阅读量PV技术方案

### SpringBoot+Redis
##### 步骤1：模拟大量PV请求
public class InitPVTask {

    @Autowired
    private RedisTemplate redisTemplate;



    @PostConstruct
    public void initPV(){
        log.info("启动模拟大量PV请求 定时器..........");
        new Thread(()->runArticlePV()).start();
    }


    /**
     * 模拟大量PV请求
     */
    public void runArticlePV() {
        while (true){
            this.batchAddArticle();
            try {
                //5秒执行一次
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 对1000篇文章，进行模拟请求PV
     */
    public void   batchAddArticle() {
        for (int i = 0; i < 1000; i++) {
            this.addPV(new Integer(i));
        }
    }

    /**
     *那如何切割时间块呢？ 如何把当前的时间切入时间块中？
     * 例如,我们要计算“小时块”，先把当前的时间转换为为毫秒的时间戳，然后除以一个小时，
     * 即当前时间T/1000*60*60=小时key，然后用这个小时序号作为key。
     * 例如：
     * 2020-01-12 15:30:00=1578814200000毫秒 转换小时key=1578814200000/1000*60*60=438560
     * 2020-01-12 15:59:00=1578815940000毫秒 转换小时key=1578815940000/1000*60*60=438560
     * 2020-01-12 16:30:00=1578817800000毫秒 转换小时key=1578817800000/1000*60*60=438561
     * 剩下的以此类推
     *
     * 每一次PV操作时，先计算当前时间是那个时间块，然后存储Map中。
     */
    public void addPV(Integer id) {
        //生成环境：时间块为5分钟
        //long m5=System.currentTimeMillis()/(1000*60*5);
        //为了方便测试 改为1分钟 时间块
        long m1=System.currentTimeMillis()/(1000*60*1);
        Map<Integer,Integer> mMap=Constants.PV_MAP.get(m1);
        if (CollectionUtils.isEmpty(mMap)){
            mMap=new ConcurrentHashMap();
            mMap.put(id,new Integer(1));
            //<1分钟的时间块，Map<文章Id,访问量>>
            Constants.PV_MAP.put(m1, mMap);
        }else {
            //通过文章id 取出浏览量
            Integer value=mMap.get(id);
            if (value==null){
                mMap.put(id,new Integer(1));
            }else{
                mMap.put(id,value+1);
            }
        }
    }


}


##### 步骤2：一级缓存定时器消费
``` 
public class OneCacheTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void cacheTask(){
        log.info("启动定时器：一级缓存消费..........");
        new Thread(()->runCache()).start();
    }




    /**
     * 一级缓存定时器消费
     * 定时器，定时（5分钟）从jvm的map把时间块的阅读pv取出来，
     * 然后push到reids的list数据结构中，list的存储的书为Map<文章id，访问量PV>即每个时间块的pv数据
     */
    public void runCache() {
        while (true){
            this.consumePV();
            try {
                //间隔1.5分钟 执行一遍
                Thread.sleep(90000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("消费一级缓存，定时刷新..............");
        }
    }


    public void consumePV(){
        //为了方便测试 改为1分钟 时间块
        long m1=System.currentTimeMillis()/(1000*60*1);
        Iterator<Long> iterator= Constants.PV_MAP.keySet().iterator();
        while (iterator.hasNext()){
            //取出map的时间块
            Long key=iterator.next();
            //小于当前的分钟时间块key ，就消费
            if (key<m1){
                //先push
                Map<Integer,Integer> map=Constants.PV_MAP.get(key);
                //push到reids的list数据结构中，list的存储的书为Map<文章id，访问量PV>即每个时间块的pv数据
                this.redisTemplate.opsForList().leftPush(Constants.CACHE_PV_LIST,map);
                //后remove
                Constants.PV_MAP.remove(key);
                log.info("push进{}",map);
            }
        }
    }


}
```


##### 步骤3：二级缓存定时器消费
``` 
public class TwoCacheTask {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void cacheTask(){
        log.info("启动定时器：二级缓存消费..........");
        new Thread(()->runCache()).start();
    }

    /**
     * 二级缓存定时器消费
     * 定时器，定时（6分钟），从redis的list数据结构pop弹出Map<文章id，访问量PV>，弹出来做了2件事：
     * 第一件事：先把Map<文章id，访问量PV>，保存到数据库
     * 第二件事：再把Map<文章id，访问量PV>，同步到redis缓存的计数器incr。
     */
    public void runCache() {
        while (true){
            while (this.pop()){

            }

            try {
                //间隔2分钟 执行一遍
                Thread.sleep(1000*60*2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("消费二级缓存，定时刷新..............");
        }
    }


    public boolean pop(){
        //从redis的list数据结构pop弹出Map<文章id，访问量PV>
        ListOperations<String, Map<Integer,Integer>> operations= this.redisTemplate.opsForList();
        Map<Integer,Integer> map= operations.rightPop(Constants.CACHE_PV_LIST);
        log.info("弹出pop={}",map);
        if (CollectionUtils.isEmpty(map)){
            return false;
        }
        // 第一步：先存入数据库
        // TODO: 插入数据库

        //第二步：同步redis缓存
        for (Map.Entry<Integer,Integer> entry:map.entrySet()){
//            log.info("key={},value={}",entry.getKey(),entry.getValue());
            String key=Constants.CACHE_ARTICLE+entry.getKey();
            //调用redis的increment命令
            long n=this.redisTemplate.opsForValue().increment(key,entry.getValue());
//            log.info("key={},pv={}",key, n);
        }
        return true;
    }


}
```


##### 步骤4：查看浏览量

``` 
    @GetMapping(value = "/view")
    public String view(Integer id) {
        String key= Constants.CACHE_ARTICLE+id;
        //调用redis的get命令
        String n=this.stringRedisTemplate.opsForValue().get(key);
        log.info("key={},阅读量为{}",key, n);
        return n;
    }
```
