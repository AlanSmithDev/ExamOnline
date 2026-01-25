package com.sdut.examonline.message.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/4/5 20:15
 */
@Data
public class WsMessageDto extends BaseDto {

    private Long userId;

    private String type;

    private String content;

    private String options;

}
