package com.sdut.examonline.log;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Chen
 * @since 2022/4/7 20:23
 */
@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.log.mapper")
@ComponentScan("com.sdut.examonline")
public class LogServiceBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(LogServiceBootstrap.class, args);
    }

}
