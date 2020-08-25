### 案例实战：SpringBoot+Redis+Cookies实现高并发的购物车
#### 步骤1：代码逻辑
``` 
    /**
     * 添加购物车
     */
    @PostMapping(value = "/addCart")
    public void addCart(CookieCart obj) {
        String cartId=this.getCookiesCartId();
        String key=COOKIE_KEY+cartId;
        Boolean hasKey = redisTemplate.opsForHash().getOperations().hasKey(key);
        //存在
        if(hasKey){
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(),obj.getAmount());
        }else{
            this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(), obj.getAmount());
            this.redisTemplate.expire(key,90, TimeUnit.DAYS);
        }
    }

    @PostMapping(value = "/updateCart")
    public void updateCart(CookieCart obj) {
        String cartId=this.getCookiesCartId();
        String key=COOKIE_KEY+cartId;
        this.redisTemplate.opsForHash().put(key, obj.getProductId().toString(),obj.getAmount());
    }
    /**
     * 删除购物车
     */
    @PostMapping(value = "/delCart")
    public void delCart(Long productId) {
        String cartId=this.getCookiesCartId();
        String key=COOKIE_KEY+cartId;
        this.redisTemplate.opsForHash().delete(key, productId.toString());
    }
    /**
     * 查询某个用户的购物车
     */
    @PostMapping(value = "/findAll")
    public CartPage findAll() {
        String cartId=this.getCookiesCartId();
        String key=COOKIE_KEY+cartId;

        CartPage<CookieCart> cartPage=new CartPage();
        //查询该用户购物车的总数
        long size=this.redisTemplate.opsForHash().size(key);
        cartPage.setCount((int)size);

        //查询购物车的所有商品
        Map<String,Integer> map= this.redisTemplate.opsForHash().entries(key);
        List<CookieCart> cartList=new ArrayList<>();
        for (Map.Entry<String,Integer> entry:map.entrySet()){
            CookieCart cart=new CookieCart();
            cart.setProductId(Long.parseLong(entry.getKey()));
            cart.setAmount(entry.getValue());
            cartList.add(cart);
        }
        cartPage.setCartList(cartList);
        return cartPage;
    }


    /**
     * 获取cookies
     */
    public  String getCookiesCartId(){
        //第一步：先检查cookies是否有cartid
        Cookie[] cookies =  request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("cartId")){
                    return cookie.getValue();
                }
            }
        }
        //第二步：cookies没有cartid，直接生成全局id，并设置到cookie里面
        //生成全局唯一id
        long id=this.idGenerator.incrementId();
        //设置到cookies
        Cookie cookie=new Cookie("cartId",String.valueOf(id));
        response.addCookie(cookie);
        return id+"";
    }

    /**
     * 合并购物车
     * 把cookie中的购物车合并到登录用户的购物车
     */
    @PostMapping(value = "/mergeCart")
    public void mergeCart(Long userId) {
        //第一步：提取未登录用户的cookie的购物车数据
        String cartId=this.getCookiesCartId();
        String keycookie=COOKIE_KEY+cartId;
        Map<String,Integer> map= this.redisTemplate.opsForHash().entries(keycookie);

        //第二步：把cookie中得购物车合并到登录用户的购物车
        String keyuser = "cart:user:" + userId;
        this.redisTemplate.opsForHash().putAll(keyuser,map);

        //第三步：删除redis未登录的用户cookies的购物车数据
        this.redisTemplate.delete(keycookie);

        //第四步：删除未登录用户cookies的cartid
        Cookie cookie=new Cookie("cartId",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
```
#### 步骤2：用swagger体验
http://127.0.0.1:9090/swagger-ui.html#/cookie-cart-controller