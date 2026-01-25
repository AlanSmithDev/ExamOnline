package com.sdut.examonline.gateway.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.Executor;

@Component
public class NacosDynamicRouteService implements ApplicationEventPublisherAware {

    private static final String DATA_ID = "gateway-routes";

    private static final String GROUP = "DEFAULT_GROUP";
    private final List<String> routes = Lists.newArrayList();
    @Value("${spring.cloud.nacos.config.server-addr}")
    String serverAddr;
    @Autowired
    RouteDefinitionWriter routeDefinitionWriter;
    ApplicationEventPublisher applicationEventPublisher;

    @PostConstruct
    public void dynamicRoutes() {
        try {
            ConfigService configService = NacosFactory.createConfigService(serverAddr);
            configService.getConfig(DATA_ID, GROUP, 5000L);
            configService.addListener(DATA_ID, GROUP, new Listener() {

                @Override
                public void receiveConfigInfo(String configInfo) {
                    clearRoute();
                    try {
                        List<RouteDefinition> gatewayRouteDefinitions = JsonUtils.from(configInfo, new TypeReference<List<RouteDefinition>>() {
                        });
                        for (RouteDefinition routeDefinition : gatewayRouteDefinitions) {
                            addRoute(routeDefinition);
                        }
                        publish();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public Executor getExecutor() {
                    return null;
                }

            });
        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    private void clearRoute() {
        for (String id : routes) {
            this.routeDefinitionWriter.delete(Mono.just(id)).subscribe();
        }
        routes.clear();
    }

    private void addRoute(RouteDefinition definition) {
        try {
            routeDefinitionWriter.save(Mono.just(definition)).subscribe();
            routes.add(definition.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void publish() {
        this.applicationEventPublisher.publishEvent(new RefreshRoutesEvent(this.routeDefinitionWriter));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}