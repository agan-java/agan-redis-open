
### 支付宝天天抽奖实战
#### 一、支付宝天天抽奖的业务场景分析


#### 二、支付宝抽奖的技术方案
思考一个问题：支付宝的抽奖 和 京东京豆的抽奖有什么区别？？？？
1. 京豆抽奖：奖品是可以重复，例如抽5京豆可以再抽到5京豆，即京豆是无限量抽。
2. 支付宝抽奖： 奖品不能重复抽，例如1万人抽1台华为手机；再给大家举一个熟悉的例子：
 例如公司年会，抽中奖品的人，下一轮就不能重复抽取，不然就会重复中奖。

技术方案和京东的京豆类似，但是不同的是
京东的京豆用了srandmember命令，即随机返回set的一个元素
支付宝的抽奖要用spop命令，即随机返回并删除set中一个元素
为什么呢？
因为支付宝的奖品有限，不能重复抽，故抽奖完后，必须从集合中剔除中奖的人。
再举个每个人都参与过的例子，年会抽奖，你公司1000人，年会抽奖3等奖500名100元，2等奖50名1000元，1等奖10名10000元，
在抽奖的设计中就必须把已中奖的人剔除，不然就会出现重复中奖的概率。


#### 三、案例实战：SpringBoot+Redis 实现支付宝抽奖
#####步骤1：初始化抽奖数据
``` 
    /**
     *提前先把数据刷新到redis缓存中。
     */
    @PostConstruct
    public void init(){
        log.info("启动初始化..........");

        boolean bo=this.redisTemplate.hasKey(Constants.PRIZE_KEY);
        if(!bo){
            List<Integer> crowds=this.prize();
            crowds.forEach(t->this.redisTemplate.opsForSet().add(Constants.PRIZE_KEY,t));
        }
    }

    /**
     * 模拟10个用户来抽奖 list存放的是用户id
     * 例如支付宝参与抽奖，就把用户id加入set集合中
     * 例如公司抽奖，把公司所有的员工，工号都加入到set集合中
     */
    public List<Integer> prize() {
        List<Integer> list=new ArrayList<>();
        for(int i=1;i<=10;i++){
            list.add(i);
        }
        return list;
    }
```
#####步骤2：抽奖逻辑
``` 
    @GetMapping(value = "/prize")
    public List<Integer> prize(int num) {
        try {
            SetOperations<String, Integer> setOperations= this.redisTemplate.opsForSet();
            //spop命令，即随机返回并删除set中一个元素
            List<Integer> objs = setOperations.pop(Constants.PRIZE_KEY,num);
            log.info("查询结果：{}", objs);
            return  objs;
        } catch (Exception ex) {
            log.error("exception:", ex);
        }
        return null;
    }
```
#####步骤3：体验