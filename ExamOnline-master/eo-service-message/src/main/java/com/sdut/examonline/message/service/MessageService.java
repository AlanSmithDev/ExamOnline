package com.sdut.examonline.message.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.message.dto.MessageCreateRequest;
import com.sdut.examonline.message.dto.MessageSendRequest;
import com.sdut.examonline.message.dto.WsMessageDto;
import com.sdut.examonline.message.entity.Message;
import com.sdut.examonline.message.vo.MessageDetailVo;
import com.sdut.examonline.message.vo.MessageVo;

import java.util.List;

public interface MessageService extends IService<Message> {

    MessageVo createMessage(MessageCreateRequest request);

    void sendMessage(MessageSendRequest request);

    List<MessageDetailVo> myMessages();

    Boolean readMessage(Long messageId);

    Boolean deleteMessage(Long messageId);

    void pushMessage(List<Long> receivers, WsMessageDto messageDto);

    String getInternalToken(String accessToken);

}
