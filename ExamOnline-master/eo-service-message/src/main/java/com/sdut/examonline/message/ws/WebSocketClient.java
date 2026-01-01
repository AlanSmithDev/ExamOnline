package com.sdut.examonline.message.ws;

import com.google.common.collect.Sets;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.JwtTokenUtils;
import com.sdut.examonline.message.dto.WsMessageDto;
import com.sdut.examonline.message.service.MessageService;
import com.sdut.examonline.web.util.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
@ServerEndpoint(value = "/ws/message/{token}")
public class WebSocketClient {

    private static final CloseReason ACCESS_DENIED_REASON = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "拒绝访问");

    private static final CopyOnWriteArraySet<WebSocketClient> CLIENTS = new CopyOnWriteArraySet<>();
    private Session session;
    private Long userId;
    private Integer platform;

    public static Set<WebSocketClient> getClients(Long userId) {
        Set<WebSocketClient> clients = Sets.newHashSet();
        for (WebSocketClient client : CLIENTS) {
            if (client.getUserId().equals(userId)) {
                clients.add(client);
            }
        }
        return clients;
    }

    public static WebSocketClient getClient(Long userId, Integer platform) {
        for (WebSocketClient client : CLIENTS) {
            if (client.getUserId().equals(userId) && client.getPlatform().equals(platform)) {
                return client;
            }
        }
        return null;
    }

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) throws IOException {
        String token = session.getPathParameters().getOrDefault("token", null);
        if (StringUtils.isEmpty(token)) {
            session.close(ACCESS_DENIED_REASON);
            return;
        }
        MessageService messageService = SpringUtils.getBean(MessageService.class);
        String internalToken = messageService.getInternalToken(token);
        if (StringUtils.isEmpty(internalToken) || JwtTokenUtils.isExpiration(internalToken)) {
            session.close(ACCESS_DENIED_REASON);
            return;
        }
        this.session = session;
        this.userId = JwtTokenUtils.getUserId(internalToken);
        this.platform = JwtTokenUtils.getPlatform(internalToken);
        CLIENTS.add(this);
        log.info("[WS] Connected. UserId={}, Platform={}", userId, platform);
    }

    @OnClose
    public void onClose() {
        if (userId != null) {
            CLIENTS.remove(this);
            log.info("[WS] Disconnected. UserId={}, Platform={}", userId, platform);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        log.info("userId={},platform={},msg={}", userId, platform, message);
    }

    @OnError
    public void onError(Throwable error) {
        log.error(error.getMessage(), error);
    }

    public Long getUserId() {
        return userId;
    }

    public Integer getPlatform() {
        return platform;
    }

    public void sendMessage(WsMessageDto message) {
        try {
            this.session.getAsyncRemote().sendText(JsonUtils.toJson(message));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WebSocketClient that = (WebSocketClient) o;
        return Objects.equals(getUserId(), that.getUserId()) && Objects.equals(getPlatform(), that.getPlatform());
    }

    @Override
    public int hashCode() {
        int result = getUserId().hashCode();
        result = 31 * result + getPlatform().hashCode();
        return result;
    }
}
