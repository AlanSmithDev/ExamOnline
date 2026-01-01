package com.sdut.examonline.depart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.depart.mapper")
@ComponentScan("com.sdut.examonline")
public class DepartServiceBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(DepartServiceBootstrap.class, args);
    }

}
