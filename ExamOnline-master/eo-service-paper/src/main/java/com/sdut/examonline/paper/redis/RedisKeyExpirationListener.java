package com.sdut.examonline.paper.redis;

import com.sdut.examonline.paper.service.PaperService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/4 11:58
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    PaperService paperService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (StringUtils.isEmpty(expiredKey)) {
            return;
        }
        if (expiredKey.startsWith(RedisKeyConstants.PAPER_END_TIME)) {
            taskExecutor.execute(() -> {
                long paperId = Long.parseLong(expiredKey.replace(RedisKeyConstants.PAPER_END_TIME, ""));
                paperService.correctPaper(paperId);
            });
        }
    }
}
