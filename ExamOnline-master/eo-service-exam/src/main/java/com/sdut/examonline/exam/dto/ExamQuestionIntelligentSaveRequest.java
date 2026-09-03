package com.sdut.examonline.exam.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@ApiModel(value = "保存智能组卷题目请求", description = "保存智能组卷题目请求")
public class ExamQuestionIntelligentSaveRequest extends BaseDto {

    @NotBlank(message = "ID不能为空")
    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @NotBlank(message = "考试ID不能为空")
    @ApiModelProperty(value = "考试ID", required = true)
    private Long examId;

    @NotBlank(message = "题库ID不能为空")
    @ApiModelProperty(value = "题库ID", required = true)
    private Long repoId;

    @ApiModelProperty(value = "试题类型", required = true)
    private Integer questionType;

    @Positive(message = "抽取数量必须大于0")
    @ApiModelProperty(value = "抽取数量", required = true)
    private Integer amount;

    @Positive(message = "试题分数必须大于0")
    @ApiModelProperty(value = "试题分数", required = true)
    private Integer score;

}
