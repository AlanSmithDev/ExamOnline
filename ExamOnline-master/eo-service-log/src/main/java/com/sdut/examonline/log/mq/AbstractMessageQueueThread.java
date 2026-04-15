package com.sdut.examonline.log.mq;

import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.web.util.CacheUtils;
import io.lettuce.core.RedisCommandTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * @author Chen
 * @since 2022/4/8 14:17
 */
@Slf4j
public abstract class AbstractMessageQueueThread<T> extends Thread implements InitializingBean, ApplicationListener<ContextClosedEvent> {

    private static final Long LOOP_WAIT_MILLISECONDS = 3000L;

    @Autowired
    CacheUtils cacheUtils;

    public abstract String key();

    public abstract Class<T> type();

    public abstract void process(T data);

    @Override
    public void run() {
        final String key = key();
        final String bakKey = key + ":bak";
        while (true) {
            String strData = null;
            try {
                while(true) {
                    try {
                        strData = cacheUtils.getRedisTemplate().opsForList().rightPopAndLeftPush(key, bakKey);
                        if (StringUtils.isEmpty(strData)) {
                            Thread.sleep(LOOP_WAIT_MILLISECONDS);
                        } else {
                            break;
                        }
                    } catch (Exception ignored) {

                    }
                }
                T data = JsonUtils.from(strData, type());
                process(data);
            } catch (Throwable throwable) {
                log.error("日志记录异常, msg={},data={}", throwable.getMessage(), strData);
            } finally {
                if (strData != null) {
                    cacheUtils.getRedisTemplate().opsForList().remove(bakKey, 1, strData);
                }
            }
        }
    }

    @Override
    public void afterPropertiesSet() {
        setName(super.getClass().getSimpleName());
        if (!super.isAlive()) {
            super.start();
        }
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        super.interrupt();
    }
}
