package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

@Data
public class ExamQuestionManualDto extends BaseDto {

    private Long id;

    private Long examId;

    private Long questionId;

    private Integer score;

}