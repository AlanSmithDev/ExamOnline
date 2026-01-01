package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/3/17 10:58
 */
@Data
public class MessageDto extends BaseDto {

    private String title;

    private String content;

}
