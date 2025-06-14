package com.guangyu.guangyubackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SpringBootApplication
@MapperScan("com.guangyu.guangyubackend.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class GuangyuBackendApplication {

    public static void main(String[] args) {

        SpringApplication.run(GuangyuBackendApplication.class, args);
    }

}
