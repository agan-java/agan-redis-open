
# 京东双11购物车

### 一、京东购物车多种场景分析
步骤1：先登录你的京东账号，清空以前购物车，然后添加一件商品A，保证你的购物车只有一件商品A。  
步骤2：退出登录，购物车添加商品B，然后关闭浏览器再打开。（请问：购物车的商品B是否存在？）  
步骤3：再次登录你的京东账号。（请问：你的购物车有几件商品？）  

### 二、图解分析：双11高并发的京东购物车技术实现


### 三、购物车的redis经典场景
往购物车加入2件商品
采用hash数据结果，key=cart：user:用户id
``` 
127.0.0.1:6379> hset cart:user:1000 101 1
(integer) 1
127.0.0.1:6379> hset cart:user:1000 102 1
(integer) 1
127.0.0.1:6379> hgetall cart:user:1000
1) "101"
2) "1"
3) "102"
4) "1"
```
修改购物车的数据，为某件商品添加数量
``` 
127.0.0.1:6379> hincrby cart:user:1000 101 1
(integer) 2
127.0.0.1:6379> hincrby cart:user:1000 102 10
(integer) 11
127.0.0.1:6379> hgetall cart:user:1000
1) "101"
2) "2"
3) "102"
4) "11"
```
统计购物车有多少件商品
``` 
127.0.0.1:6379> hlen cart:user:1000
(integer) 2
```

删除购物车某件商品
``` 
127.0.0.1:6379> hdel cart:user:1000 102
(integer) 1
127.0.0.1:6379> hgetall cart:user:1000
1) "101"
2) "2"
```


### 四、SpringBoot+Redis 实现高并发购物车
#### 步骤1：登录状态下添加商品到购物车
``` 

    /**
     * 添加购物车
     */
    @PostMapping(value = "/addCart")
    public void addCart(Cart obj) {
        String key = CART_KEY + obj.getUserId();
        Boolean hasKey = redisTemplate.opsForHash().getOperations().hasKey(key);
        //存在
        if(hasKey){
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
        }else{
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
            this.redisTemplate.expire(key,90, TimeUnit.DAYS);
        }

    }

    /**
     * 修改购物车的数量
     */
    @PostMapping(value = "/updateCart")
    public void updateCart(Cart obj) {
        String key = CART_KEY + obj.getUserId();
        this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
    }

    /**
     *删除购物车
     */
    @PostMapping(value = "/delCart")
    public void delCart(Long userId, Long productId) {
        String key = CART_KEY + userId;
        this.redisTemplate.opsForHash().delete(key, productId.toString());
    }


    @PostMapping(value = "/findAll")
    public CartPage findAll(Long userId) {
        String key = CART_KEY + userId;
        CartPage cartPage = new CartPage();
        //查购物车的总数
        long size = this.redisTemplate.opsForHash().size(key);
        cartPage.setCount((int) size);

        //查询购物车的所有商品
        //entries=hgetall命令
        Map<String, Integer> map = this.redisTemplate.opsForHash().entries(key);
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            Cart cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(Long.parseLong(entry.getKey()));
            cart.setAmount(entry.getValue());
            cartList.add(cart);
        }
        cartPage.setCartList(cartList);
        return cartPage;
    }
```
#### 步骤2：用swagger体验
http://127.0.0.1:9090/swagger-ui.html#/
