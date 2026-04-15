package com.sdut.examonline.paper;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.paper.mapper")
@ComponentScan("com.sdut.examonline")
public class PaperServiceBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(PaperServiceBootstrap.class, args);
    }

}
