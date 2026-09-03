package com.sdut.examonline.message;

import com.sdut.examonline.web.config.SecurityConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Chen
 * @since 2022/4/5 14:55
 */
@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.message.mapper")
@ComponentScan("com.sdut.examonline")
public class MessageServiceBootstrap {

    public static void main(String[] args) {
        SecurityConfig.addAllowedPath("/ws/**");
        SpringApplication.run(MessageServiceBootstrap.class, args);
    }

}
