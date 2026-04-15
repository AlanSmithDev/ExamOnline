package com.sdut.examonline.message.dubbo;

import com.sdut.examonline.dubbo.client.MessageDubboService;
import com.sdut.examonline.dubbo.dto.MessageDto;
import com.sdut.examonline.message.dto.MessageCreateRequest;
import com.sdut.examonline.message.dto.MessageSendRequest;
import com.sdut.examonline.message.dto.WsMessageDto;
import com.sdut.examonline.message.service.MessageService;
import com.sdut.examonline.message.vo.MessageVo;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/4/5 21:30
 */
@Service(retries = 0)
public class MessageDubboServiceImpl implements MessageDubboService {

    @Autowired
    MessageService messageService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("message_count", messageService.count());
        }};
    }

    @Override
    public void createAndSendMessage(MessageDto messageDto, List<Long> receivers) {
        MessageVo message = messageService.createMessage(MapperUtils.map(messageDto, MessageCreateRequest.class));
        MessageSendRequest request = new MessageSendRequest();
        request.setMessageId(message.getId());
        request.setReceivers(receivers);
        messageService.sendMessage(request);
    }

    @Override
    public void pushMessage(com.sdut.examonline.dubbo.dto.WsMessageDto wsMessageDto, List<Long> receivers) {
        messageService.pushMessage(receivers, MapperUtils.map(wsMessageDto, WsMessageDto.class));
    }

}
