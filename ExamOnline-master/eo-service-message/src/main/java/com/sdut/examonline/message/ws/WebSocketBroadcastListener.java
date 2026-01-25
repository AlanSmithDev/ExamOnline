package com.sdut.examonline.message.ws;

import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.message.dto.WsMessageDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/5 20:21
 */
@Component
public class WebSocketBroadcastListener implements InitializingBean, MessageListener {

    public static final String WEB_SOCKET_BROADCASTER_TOPIC = "wsbc";
    private final RedisMessageListenerContainer listenerContainer;
    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    public WebSocketBroadcastListener(RedisMessageListenerContainer listenerContainer) {
        this.listenerContainer = listenerContainer;
    }

    @SneakyThrows
    @Override
    public void onMessage(Message message, byte[] bytes) {
        String topic = new String(bytes);
        if (WEB_SOCKET_BROADCASTER_TOPIC.equals(topic)) {
            WsMessageDto messageDto = JsonUtils.from(new String(message.getBody()), WsMessageDto.class);
            taskExecutor.execute(() -> {
                for (WebSocketClient client : WebSocketClient.getClients(messageDto.getUserId())) {
                    client.sendMessage(messageDto);
                }
            });
        }
    }

    @Override
    public void afterPropertiesSet() {
        this.listenerContainer.addMessageListener(this, new ChannelTopic(WEB_SOCKET_BROADCASTER_TOPIC));
    }
}
