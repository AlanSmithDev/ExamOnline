package com.sdut.examonline.message.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.message.dto.MessageCreateRequest;
import com.sdut.examonline.message.dto.MessageSendRequest;
import com.sdut.examonline.message.dto.WsMessageDto;
import com.sdut.examonline.message.service.MessageService;
import com.sdut.examonline.message.vo.MessageDetailVo;
import com.sdut.examonline.message.vo.MessageVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Api("消息模块")
@RestController
@RequestMapping("/api/message")
public class MessageController {

    @Autowired
    MessageService messageService;

    @LogOperation(msg = "创建消息", type = OperationType.CREATE)
    @ApiOperation(value = "创建消息")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    ResponseVo<MessageVo> createMessage(@RequestBody MessageCreateRequest request) {
        return ResponseVo.success(messageService.createMessage(request));
    }

    @LogOperation(msg = "发送消息", type = OperationType.CREATE)
    @ApiOperation(value = "发送消息")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/send", method = RequestMethod.POST)
    ResponseVo<Void> sendMessage(@RequestBody MessageSendRequest request) {
        messageService.sendMessage(request);
        return ResponseVo.success();
    }

    @LogOperation(msg = "我的消息列表", type = OperationType.QUERY)
    @ApiOperation(value = "我的消息列表")
    @RequestMapping(value = "/my", method = RequestMethod.GET)
    ResponseVo<List<MessageDetailVo>> myMessages() {
        return ResponseVo.success(messageService.myMessages());
    }

    @LogOperation(msg = "阅读消息", type = OperationType.UPDATE)
    @ApiOperation(value = "阅读消息")
    @RequestMapping(value = "/read/{messageId}", method = RequestMethod.GET)
    ResponseVo<Boolean> readMessage(@PathVariable("messageId") Long messageId) {
        return ResponseVo.success(messageService.readMessage(messageId));
    }

    @LogOperation(msg = "删除消息", type = OperationType.DELETE)
    @ApiOperation(value = "删除消息")
    @RequestMapping(value = "/delete/{messageId}", method = RequestMethod.GET)
    ResponseVo<Boolean> deleteMessage(@PathVariable("messageId") Long messageId) {
        return ResponseVo.success(messageService.deleteMessage(messageId));
    }

    @LogOperation(msg = "推送消息", type = OperationType.CREATE)
    @ApiOperation(value = "推送消息(测试使用)")
    @RequestMapping(value = "/push", method = RequestMethod.POST)
    ResponseVo<Void> push(@RequestBody WsMessageDto message) {
        messageService.pushMessage(Collections.singletonList(message.getUserId()), message);
        return ResponseVo.success();
    }

}
