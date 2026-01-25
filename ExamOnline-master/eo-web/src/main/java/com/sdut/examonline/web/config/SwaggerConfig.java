package com.sdut.examonline.web.config;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.JwtConstants;
import com.sdut.examonline.common.util.JsonUtils;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.json.Json;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@ConfigurationProperties(prefix = "swagger")
@ControllerAdvice
public class SwaggerConfig implements WebMvcConfigurer, ResponseBodyAdvice {

    @Bean
    Docket userModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("用户模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/user/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket departModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("班级模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/depart/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket questionModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("题库模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/question/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket examModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("考试模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/exam/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket paperModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("试卷模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/paper/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket fileModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("文件模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/file/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket messageModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("消息模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/message/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    @Bean
    Docket logModule() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .groupName("日志模块接口")
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.ant("/api/log/**"))
                .build()
                .securityContexts(securityContext())
                .securitySchemes(securitySchemes());
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("在线考试系统接口")
                .description("在线考试系统接口")
                .version("1.0.0")
                .build();
    }

    List<ApiKey> securitySchemes() {
        return Lists.newArrayList(new ApiKey(JwtConstants.HEADER_ACCESS_TOKEN, JwtConstants.HEADER_ACCESS_TOKEN, "header"));
    }

    List<SecurityContext> securityContext() {
        return Lists.newArrayList(SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build());
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference(JwtConstants.HEADER_ACCESS_TOKEN, authorizationScopes));
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter,
                                  MediaType mediaType, Class clazz, ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (body instanceof Json) {
            try {
                JSONObject jsonObject = JsonUtils.from(((Json) body).value(), JSONObject.class);
                jsonObject.put("basePath", "/");
                String value = JsonUtils.toJson(jsonObject);
                return new Json(value);
            } catch (Exception ignored) {
            }
        }
        return body;
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class clazz) {
        if (methodParameter.getMethod() == null) {
            return false;
        }
        return "getDocumentation".equals(methodParameter.getMethod().getName());
    }
}
