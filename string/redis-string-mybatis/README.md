# spring boot 和redis集成

技术栈：springboot mybatis swagger ,如果不懂的同学，请百度一下，
看老师以前的课程 https://study.163.com/course/courseMain.htm?courseId=1004348001&share=2&shareId=1016671292


### 步骤一：pom文件引入redis依赖包
``` 
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-redis</artifactId>
    <version>1.4.7.RELEASE</version>
</dependency>
```

### 步骤二：配置文件加入redis配置信息
``` 

## Redis 配置
## Redis数据库索引（默认为0）
spring.redis.database=0
## Redis服务器地址
spring.redis.host=192.168.1.138
## Redis服务器连接端口
spring.redis.port=6379
## Redis服务器连接密码（默认为空）
spring.redis.password=


```

### 演示RedisTemplate的增删改
controller
``` 

@Api(description = "用户接口")
@RestController
@RequestMapping("/user")
public class UserController {


    @Autowired
    private UserService userService;


    @ApiOperation("数据库初始化100条数据")
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public void init() {
        for (int i = 0; i < 100; i++) {
            Random rand = new Random();
            User user = new User();
            String temp = "un" + i;
            user.setUsername(temp);
            user.setPassword(temp);
            int n = rand.nextInt(2);
            user.setSex((byte) n);
            userService.createUser(user);
        }
    }

    @ApiOperation("单个用户查询，按userid查用户信息")
    @RequestMapping(value = "/findById/{id}", method = RequestMethod.GET)
    public UserVO findById(@PathVariable int id) {
        User user = this.userService.findUserById(id);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @ApiOperation("修改某条数据")
    @PostMapping(value = "/updateUser")
    public void updateUser(@RequestBody UserVO obj) {
        User user = new User();
        BeanUtils.copyProperties(obj, user);
        userService.updateUser(user);
    }


}
```

service

``` 
@Service
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    public static final String CACHE_KEY_USER = "user:";

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;


    public void createUser(User obj){
        this.userMapper.insertSelective(obj);

        //缓存key
        String key=CACHE_KEY_USER+obj.getId();
        //到数据库里面，重新捞出新数据出来，做缓存
        obj=this.userMapper.selectByPrimaryKey(obj.getId());

        //opsForValue代表了Redis的String数据结构
        //set代表了redis的SET命令
        redisTemplate.opsForValue().set(key,obj);
    }

    public void updateUser(User obj){
        //1.先直接修改数据库
        this.userMapper.updateByPrimaryKeySelective(obj);
        //2.再修改缓存
        //缓存key
        String key=CACHE_KEY_USER+obj.getId();
        obj=this.userMapper.selectByPrimaryKey(obj.getId());
        //修改也是用SET命令，重新设置，Redis 没有update操作，都是重新设置新值
        redisTemplate.opsForValue().set(key,obj);
    }

    public User findUserById(Integer userid){
        ValueOperations<String, User> operations = redisTemplate.opsForValue();
        //缓存key
        String key=CACHE_KEY_USER+userid;
        //1.先去redis查 ，如果查到直接返回，没有的话直接去数据库捞
        //Redis 用了GET命令
        User user=operations.get(key);
        
        //2.redis没有的话，直接去数据库捞
        if(user==null){
            user=this.userMapper.selectByPrimaryKey(userid);
            //由于redis没有才到数据库捞，所以必须把捞到的数据写入redis，方便下次查询能redis命中。
            operations.set(key,user);
        }
        return user;
    }

}
```
### 步骤体验效果：
用http://127.0.0.1:9090/swagger-ui.html# 体验
问题1：进redis的数据必须序列化Serializable

问题2：如果连接不了redis
```  
vi redis.conf
bind 0.0.0.0
```

### 优化重写Redis的序列化，改为Json方式
为什么要重写Redis序列化方式，改为Json呢？  
因为RedisTemplate默认使用的是JdkSerializationRedisSerializer，会出现2个问题：  
1. 被序列化的对象必须实现Serializable接口
``` 
@Table(name = "users")
public class User implements  Serializable {
```  
2. 被序列化会出现乱码,导致value值可读性差. 
``` 
127.0.0.1:6379> keys *
  1) "\xac\xed\x00\x05t\x00\auser:62"
  2) "\xac\xed\x00\x05t\x00\auser:65"
  3) "\xac\xed\x00\x05t\x00\auser:50"
  4) "\xac\xed\x00\x05t\x00\auser:36"
  5) "\xac\xed\x00\x05t\x00\x06user:6"
  6) "\xac\xed\x00\x05t\x00\auser:17"
  7) "\xac\xed\x00\x05t\x00\auser:28"
  
127.0.0.1:6379> get "\xac\xed\x00\x05t\x00\auser:62"
"\xac\xed\x00\x05sr\x00\x1acom.agan.redis.entity.User?\xebU\xa1\xe2\xa6\xfe\xe3\x02\x00\aL\x00\ncreateTimet
\x00\x10Ljava/util/Date;L\x00\adeletedt\x00\x10Ljava/lang/Byte;L\x00\x02idt\x00\x13Ljava/lang/Integer;L\x00
\bpasswordt\x00\x12Ljava/lang/String;L\x00\x03sexq\x00~\x00\x02L\x00\nupdateTimeq\x00~\x00\x01L\x00\buser
nameq\x00~\x00\x04xpsr\x00\x0ejava.util.Datehj\x81\x01KYt\x19\x03\x00\x00xpw\b\x00\x00\x01o+5\x1d\xf8xsr
\x00\x0ejava.lang.Byte\x9cN`\x84\xeeP\xf5\x1c\x02\x00\x01B\x00\x05valuexr\x00\x10java.lang.Number\x86\xac
\x95\x1d\x0b\x94\xe0\x8b\x02\x00\x00xp\x00sr\x00\x11java.lang.Integer\x12\xe2\xa0\xa4\xf7\x81\x878\x02\x00
\x01I\x00\x05valuexq\x00~\x00\t\x00\x00\x00>t\x00\x04un59q\x00~\x00\nsq\x00~\x00\x06w\b\x00\x00\x01o+5\x1d
\xf8xt\x00\x04un59"
```

``` 
@Configuration
public class RedisConfiguration {
    /**
     * 重写Redis序列化方式，使用Json方式:
     * 当我们的数据存储到Redis的时候，我们的键（key）和值（value）都是通过Spring提供的Serializer序列化到Redis的。
     * RedisTemplate默认使用的是JdkSerializationRedisSerializer，
     * StringRedisTemplate默认使用的是StringRedisSerializer。
     *
     * Spring Data JPA为我们提供了下面的Serializer：
     * GenericToStringSerializer、Jackson2JsonRedisSerializer、
     * JacksonJsonRedisSerializer、JdkSerializationRedisSerializer、
     * OxmSerializer、StringRedisSerializer。
     * 在此我们将自己配置RedisTemplate并定义Serializer。
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //创建一个json的序列化对象
        GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        //设置value的序列化方式json
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        //设置key序列化方式string
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        //设置hash key序列化方式string
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        //设置hash value的序列化方式json
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
}
```


#### 体验：
1. 先把user的序列化删除
2. 创建类RedisConfiguration
3. flushdb 清空redis的旧数据，因为改了序列化，老数据以及不能兼容了，必须清空旧数据
4. 往redis 初始化100条数据
5. 用 keys *   命令查看所有key
``` 
127.0.0.1:6379> keys *
  1) "user:187"
  2) "user:117"
  3) "user:170"
  4) "user:139"
  5) "user:157"
  
  
127.0.0.1:6379> get user:187
"{\"@class\":\"com.agan.redis.entity.User\",\"id\":187,\"username\":\"un84\",\"password\":\"un84\",
\"sex\":0,\"deleted\":0,\"updateTime\":[\"java.util.Date\",1576983528000],
\"createTime\":[\"java.util.Date\",1576983528000]}"
```