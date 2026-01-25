package com.sdut.examonline.file;

import com.sdut.examonline.web.config.SecurityConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@SpringBootApplication
@MapperScan("com.sdut.examonline.file.mapper")
@ComponentScan("com.sdut.examonline")
public class FileServiceBootstrap {

    public static void main(String[] args) {
        SecurityConfig.addAllowedPath(
                "/api/file/apk",
                "/api/file/ios"
        );
        SpringApplication.run(FileServiceBootstrap.class, args);
    }

}
