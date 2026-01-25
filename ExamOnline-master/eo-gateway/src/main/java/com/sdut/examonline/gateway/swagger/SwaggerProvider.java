package com.sdut.examonline.gateway.swagger;

import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.*;

@Component
public class SwaggerProvider implements SwaggerResourcesProvider {

    /**
     * swagger2默认的url后缀
     */
    private static final String SWAGGER2URL = "/v2/api-docs?group=";

    /**
     * 网关应用名称
     * 配置集成的服务，名称与gateway中route的Path一致，用于拼接路径访问后端服务的swagger路径
     * 如：swagger.serivces = rider,user
     */
    private static final Map<String, String> DOCKETS = new HashMap<String, String>() {{
        put("/api/user", "用户模块接口");
        put("/api/file", "文件模块接口");
        put("/api/depart", "班级模块接口");
        put("/api/question", "题库模块接口");
        put("/api/exam", "考试模块接口");
        put("/api/paper", "试卷模块接口");
        put("/api/message", "消息模块接口");
        put("/api/log", "日志模块接口");
    }};

    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = Lists.newArrayList();
        Set<String> set = new HashSet<>();
        for (Map.Entry<String, String> entry : DOCKETS.entrySet()) {
            String url = entry.getKey() + SWAGGER2URL + entry.getValue();
            if (!set.contains(url)) {
                set.add(url);
                SwaggerResource swaggerResource = new SwaggerResource();
                swaggerResource.setLocation(url);
                swaggerResource.setUrl(url);
                swaggerResource.setName(entry.getValue());
                swaggerResource.setSwaggerVersion("2.0");
                resources.add(swaggerResource);
            }
        }
        return resources;
    }
}
