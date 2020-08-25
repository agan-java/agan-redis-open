package com.agan.redis.controller;


import com.agan.redis.entity.User;
import com.agan.redis.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
/**
 * @author 阿甘
 * @see https://study.163.com/provider/1016671292/course.htm?share=1&shareId=1016481220
 * @version 1.0
 * 注：如有任何疑问欢迎阿甘老师微信：agan-java 随时咨询老师。
 */
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
