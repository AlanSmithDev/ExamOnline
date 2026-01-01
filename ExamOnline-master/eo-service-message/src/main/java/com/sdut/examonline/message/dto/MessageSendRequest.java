package com.sdut.examonline.message.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "发送消息请求", description = "发送消息请求")
public class MessageSendRequest extends BaseDto {

    @ApiModelProperty(value = "消息ID", required = true)
    private Long messageId;

    @ApiModelProperty(value = "接收者列表", required = true)
    private List<Long> receivers;

}
