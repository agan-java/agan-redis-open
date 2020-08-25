package com.agan.redis;

import com.agan.redis.task.InitService;
import com.agan.redis.task.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringBootMybatisApplicationTests {

    @Autowired
    InitService initService;
    @Test
    public void contextLoads() {
        this.initService.init30day();
    }

}
