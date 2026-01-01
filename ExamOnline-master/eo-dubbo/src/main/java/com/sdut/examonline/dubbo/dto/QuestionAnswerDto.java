package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

@Data
public class QuestionAnswerDto extends BaseDto {

    private Long id;

    private String content;

    private Boolean isRight;

    private Long questionId;

}