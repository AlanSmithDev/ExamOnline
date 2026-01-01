package com.sdut.examonline.web.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.sdut.examonline.common.constant.CacheConstants;
import com.sdut.examonline.web.util.CacheUtils;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Chen
 * @since 2022/3/31 16:39
 */
@Configuration
public class CacheConfig {

    @Autowired
    RedisConnectionFactory connectionFactory;

    @Autowired
    CacheUtils cacheUtils;

    @Bean
    Cache<String, Object> cache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(CacheConstants.CAFFEINE_EXPIRE_SECONDS, TimeUnit.SECONDS)
                .initialCapacity(CacheConstants.CAFFEINE_INITIAL_CAPACITY)
                .maximumSize(CacheConstants.CAFFEINE_MAXIMUM_SIZE)
                .build();
    }

    @Bean
    RedisMessageListenerContainer redisMessageListenerContainer() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setTaskExecutor(new ThreadPoolExecutor(4, 4,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), new NamedThreadFactory("redis-executor")));
        container.addMessageListener(cacheUtils, new ChannelTopic(CacheUtils.DELETE_KEY_TOPIC));
        return container;
    }

}
