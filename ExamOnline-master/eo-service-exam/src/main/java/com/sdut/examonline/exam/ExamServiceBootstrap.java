package com.sdut.examonline.exam;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.exam.mapper")
@ComponentScan("com.sdut.examonline")
public class ExamServiceBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(ExamServiceBootstrap.class, args);
    }

}
