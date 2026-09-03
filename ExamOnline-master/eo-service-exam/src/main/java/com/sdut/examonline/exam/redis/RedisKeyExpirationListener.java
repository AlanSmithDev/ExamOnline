package com.sdut.examonline.exam.redis;

import com.sdut.examonline.exam.service.ExamService;
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
 * @since 2022/4/10 10:05
 */
@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    ExamService examService;

    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        if (StringUtils.isEmpty(expiredKey)) {
            return;
        }
        if (expiredKey.startsWith(RedisKeyConstants.EXAM_START_TIME)) {
            taskExecutor.execute(() -> {
                long examId = Long.parseLong(expiredKey.replace(RedisKeyConstants.EXAM_START_TIME, ""));
                examService.pushPrestart(examId);
            });
        }
    }
}
