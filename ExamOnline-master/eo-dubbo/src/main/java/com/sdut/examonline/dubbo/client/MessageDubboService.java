package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.dubbo.dto.MessageDto;
import com.sdut.examonline.dubbo.dto.WsMessageDto;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:26
 */
public interface MessageDubboService {

    Map<String, Object> getAdminDashboard();

    void createAndSendMessage(MessageDto messageDto, List<Long> receivers);

    void pushMessage(WsMessageDto wsMessageDto, List<Long> receivers);

}
