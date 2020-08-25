package com.agan.redis.service;





import com.agan.redis.entity.User;
import com.agan.redis.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
@Service
@CacheConfig(cacheNames = { "user" })
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserMapper userMapper;

    @Cacheable(key="#id")
    public User findUserById(Integer id){
        return this.userMapper.selectByPrimaryKey(id);
    }

    @CachePut(key = "#obj.id")
    public User updateUser(User obj){
        this.userMapper.updateByPrimaryKeySelective(obj);
        return this.userMapper.selectByPrimaryKey(obj.getId());
    }

    @CacheEvict(key = "#id")
    public void deleteUser(Integer id){
        User user=new User();
        user.setId(id);
        user.setDeleted((byte)1);
        this.userMapper.updateByPrimaryKeySelective(user);
    }

}
