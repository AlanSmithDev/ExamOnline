package com.sdut.examonline.user;

import com.sdut.examonline.web.config.SecurityConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.user.mapper")
@ComponentScan("com.sdut.examonline")
public class UserServiceBootstrap {

    public static void main(String[] args) {
        SecurityConfig.addAllowedPath(
                "/api/user/login",
                "/api/user/register",
                "/api/user/qrcode/create",
                "/api/user/qrcode/check/**",
                "/api/user/qrcode/login/**",
                "/api/user/face/**");
        SpringApplication.run(UserServiceBootstrap.class, args);
    }

}
