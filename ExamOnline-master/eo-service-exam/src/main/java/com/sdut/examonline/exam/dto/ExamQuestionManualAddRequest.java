package com.sdut.examonline.exam.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "创建手动组卷题目请求", description = "创建手动组卷题目请求")
public class ExamQuestionManualAddRequest extends BaseDto {

    @NotBlank(message = "考试ID不能为空")
    @ApiModelProperty(value = "考试ID", required = true)
    private Long examId;

    @NotBlank(message = "所选题目不能为空")
    @ApiModelProperty(value = "试题ID", required = true)
    private Long questionId;

    @Positive(message = "试题分数必须大于0")
    @ApiModelProperty(value = "试题分数", required = true)
    private Integer score;

}
