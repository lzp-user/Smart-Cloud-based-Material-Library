package com.lzp.lpicturebac;

import org.apache.shardingsphere.spring.boot.ShardingSphereAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

//关闭分库分表 exclude = {ShardingSphereAutoConfiguration.class}
@SpringBootApplication(exclude = {ShardingSphereAutoConfiguration.class})
@EnableAsync  //开启@Async异步支持
@MapperScan("com.lzp.lpicturebac.mapper")  //mybatis-plus
@EnableAspectJAutoProxy(exposeProxy = true)  //代理
public class LPictureBacApplication {

    public static void main(String[] args) {
        SpringApplication.run(LPictureBacApplication.class, args);
    }

}
