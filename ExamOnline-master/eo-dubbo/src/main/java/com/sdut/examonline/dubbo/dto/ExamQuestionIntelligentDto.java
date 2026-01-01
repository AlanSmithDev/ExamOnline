package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

@Data
public class ExamQuestionIntelligentDto extends BaseDto {

    private Long id;

    private Long examId;

    private Long repoId;

    private Integer questionType;

    private Integer amount;

    private Integer score;

    private Integer difficulty;

}