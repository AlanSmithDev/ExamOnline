package com.sdut.examonline.web.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Configuration;

/**
 * @author Chen
 * @since 2022/3/17 8:26
 */
@Configuration
@EnableDiscoveryClient
@EnableDubbo
public class WebConfig {
}
