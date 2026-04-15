package com.sdut.examonline.message.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.dubbo.client.UserDubboService;
import com.sdut.examonline.message.dto.MessageCreateRequest;
import com.sdut.examonline.message.dto.MessageSendRequest;
import com.sdut.examonline.message.dto.WsMessageDto;
import com.sdut.examonline.message.entity.Message;
import com.sdut.examonline.message.entity.MessageReadRecord;
import com.sdut.examonline.message.mapper.MessageMapper;
import com.sdut.examonline.message.service.MessageReadRecordService;
import com.sdut.examonline.message.service.MessageService;
import com.sdut.examonline.message.vo.MessageDetailVo;
import com.sdut.examonline.message.vo.MessageVo;
import com.sdut.examonline.message.ws.WebSocketBroadcastListener;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Chen
 * @since 2022/4/5 15:07
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    MessageReadRecordService messageReadRecordService;

    @Autowired
    CacheUtils cacheUtils;

    @Reference(check = false)
    UserDubboService userDubboService;

    @Override
    public MessageVo createMessage(MessageCreateRequest request) {
        Message message = new Message();
        message.setId(SnowFlakeIdWorker.nextId());
        message.setTitle(request.getTitle());
        message.setContent(request.getContent());
        message.setCreateTime(new Date());

        super.save(message);

        cacheUtils.set(getCacheKeyMessage(message.getId()), message, 10L, TimeUnit.MINUTES);

        return MapperUtils.map(message, MessageVo.class);
    }

    @Override
    public void sendMessage(MessageSendRequest request) {
        if (CollectionUtils.isNotEmpty(request.getReceivers())) {
            Message message = getMessageInner(request.getMessageId());
            Assert.notNull(message, ErrorCode.MESSAGE_NOT_FOUND);

            List<MessageReadRecord> readRecords = Lists.newArrayList();
            for (Long receiver : request.getReceivers()) {
                MessageReadRecord readRecord = new MessageReadRecord();
                readRecord.setId(SnowFlakeIdWorker.nextId());
                readRecord.setMId(message.getId());
                readRecord.setUserId(receiver);
                readRecord.setIsRead(false);
                readRecord.setReadTime(null);
                readRecord.setIsDelete(false);
                readRecord.setDeleteTime(null);
                readRecords.add(readRecord);
            }
            messageReadRecordService.saveBatch(readRecords);

            if (CollectionUtils.isNotEmpty(request.getReceivers())) {
                WsMessageDto wsMessageDto = new WsMessageDto();
                wsMessageDto.setType("new_message");
                wsMessageDto.setContent("你收到了一条系统消息");
                JSONObject options = new JSONObject();
                options.put("app_url", "/pages/message/message");
                wsMessageDto.setOptions(options.toJSONString());
                taskExecutor.execute(() -> pushMessage(request.getReceivers(), wsMessageDto));
            }
        }
    }

    @Override
    public List<MessageDetailVo> myMessages() {
        String str = getMessageReadRecordListInner(JwtUtils.getUserId());
        List<Map<String, Object>> records = Lists.newArrayList();
        if (StringUtils.isNotBlank(str)) {
            for (String s : Splitter.on(",").split(str)) {
                String[] split = s.split("#");
                Map<String, Object> map = Maps.newHashMapWithExpectedSize(3);
                map.put("mid", Long.parseLong(split[0]));
                map.put("read", Integer.parseInt(split[1]) == 1);
                map.put("time", new Date(Long.parseLong(split[2])));
                records.add(map);
            }
        }
        if (CollectionUtils.isEmpty(records)) {
            return Collections.emptyList();
        }
        List<MessageDetailVo> messages = Lists.newArrayList();
        for (Map<String, Object> readRecord : records) {
            Message message = getMessageInner((Long) readRecord.get("mid"));
            MessageDetailVo vo = new MessageDetailVo();
            MapperUtils.map(message, vo);
            vo.setIsRead((Boolean) readRecord.get("read"));
            vo.setReadTime((Date) readRecord.get("time"));
            messages.add(vo);
        }
        return messages;
    }

    @Override
    public Boolean readMessage(Long messageId) {
        MessageReadRecord readRecord = getMessageReadRecordInner(messageId, JwtUtils.getUserId());
        Assert.notNull(readRecord, ErrorCode.MESSAGE_NOT_FOUND);

        if (readRecord.getIsRead() == null || !readRecord.getIsRead()) {
            readRecord.setIsRead(true);
            readRecord.setReadTime(new Date());
            messageReadRecordService.updateById(readRecord);
            cacheUtils.delete(getCacheKeyMessageRecord(readRecord.getMId(), readRecord.getUserId()));
            cacheUtils.delete(getCacheKeyMessageRecordList(readRecord.getUserId()));
        }
        return true;
    }

    @Override
    public Boolean deleteMessage(Long messageId) {
        MessageReadRecord readRecord = getMessageReadRecordInner(messageId, JwtUtils.getUserId());
        Assert.notNull(readRecord, ErrorCode.MESSAGE_NOT_FOUND);

        if (readRecord.getIsDelete() == null || !readRecord.getIsDelete()) {
            readRecord.setIsDelete(true);
            readRecord.setDeleteTime(new Date());
            messageReadRecordService.updateById(readRecord);
            cacheUtils.delete(getCacheKeyMessageRecord(readRecord.getMId(), readRecord.getUserId()));
            cacheUtils.delete(getCacheKeyMessageRecordList(readRecord.getUserId()));
        }

        return true;
    }

    @Override
    public void pushMessage(List<Long> receivers, WsMessageDto messageDto) {
        for (Long receiver : receivers) {
            messageDto.setUserId(receiver);
            try {
                cacheUtils.getRedisTemplate().convertAndSend(WebSocketBroadcastListener.WEB_SOCKET_BROADCASTER_TOPIC, JsonUtils.toJson(messageDto));
            } catch (Exception ignored) {

            }
        }
    }

    @Override
    public String getInternalToken(String accessToken) {
        return userDubboService.getInternalToken(accessToken);
    }

    private Message getMessageInner(Long messageId) {
        Object value = cacheUtils.get(getCacheKeyMessage(messageId), Message.class, 10, TimeUnit.MINUTES);
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
        if (value != null) {
            return (Message) value;
        }
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Message::getId, messageId);
        Message message = super.getOne(wrapper, false);
        if (message != null) {
            cacheUtils.set(getCacheKeyMessage(message.getId()), message, 10, TimeUnit.MINUTES);
        }
        return message;
    }

    private MessageReadRecord getMessageReadRecordInner(Long messageId, Long userId) {
        Object value = cacheUtils.get(getCacheKeyMessageRecord(messageId, userId), MessageReadRecord.class, 10, TimeUnit.MINUTES);
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
        if (value != null) {
            return (MessageReadRecord) value;
        }
        QueryWrapper<MessageReadRecord> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(MessageReadRecord::getMId, messageId).eq(MessageReadRecord::getUserId, userId);
        MessageReadRecord messageReadRecord = messageReadRecordService.getOne(wrapper, false);
        if (messageReadRecord != null) {
            cacheUtils.set(getCacheKeyMessageRecord(messageReadRecord.getMId(), messageReadRecord.getUserId()), messageReadRecord, 10, TimeUnit.MINUTES);
        }
        return messageReadRecord;
    }

    private String getMessageReadRecordListInner(Long userId) {
        String value = cacheUtils.getString(getCacheKeyMessageRecordList(userId), 10, TimeUnit.MINUTES);
        if (cacheUtils.isNullString(value)) {
            throw new ServiceException(ErrorCode.USER_NOT_FOUND);
        }
        if (value != null) {
            return value;
        }
        LambdaQueryWrapper<MessageReadRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MessageReadRecord::getUserId, JwtUtils.getUserId())
                .eq(MessageReadRecord::getIsDelete, false);
        List<MessageReadRecord> readRecords = messageReadRecordService.list(wrapper);
        if (readRecords.isEmpty()) {
            cacheUtils.setString(getCacheKeyMessageRecordList(userId), "", 10, TimeUnit.MINUTES);
            return StringUtils.EMPTY;
        } else {
            StringBuilder sb = new StringBuilder();
            for (MessageReadRecord readRecord : readRecords) {
                sb.append(readRecord.getMId());
                sb.append("#");
                sb.append(readRecord.getIsRead() != null && readRecord.getIsRead() ? 1 : 0);
                sb.append("#");
                sb.append(readRecord.getReadTime() == null ? 0L : readRecord.getReadTime().getTime());
                sb.append(",");
            }
            String str = sb.toString();
            str = str.substring(0, str.length() - 1);
            cacheUtils.setString(getCacheKeyMessageRecordList(userId), str, 10, TimeUnit.MINUTES);
            return str;
        }
    }

    private String getCacheKeyMessage(Long messageId) {
        return "msg:id:" + messageId;
    }

    private String getCacheKeyMessageRecord(Long messageId, Long userId) {
        return "msgrrd:" + messageId + ":" + userId;
    }

    private String getCacheKeyMessageRecordList(Long userId) {
        return "msgrrd:ls:" + userId;
    }

}
