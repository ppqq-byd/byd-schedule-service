package com.ora.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@MapperScan("com.ora.blockchain.mybatis.mapper")
@PropertySource(value = {"classpath:application.yml"}, ignoreResourceNotFound = true)
@EnableCaching
@EnableAspectJAutoProxy(exposeProxy=true)
@EnableAsync
@Slf4j
@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(Application.class);
        springApplication.setRegisterShutdownHook(true);
        springApplication.run(args);
        log.info("server started......");
    }
}
