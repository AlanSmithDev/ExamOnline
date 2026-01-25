package com.sdut.examonline.exam.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "手动组卷试题前端展示对象", description = "手动组卷试题前端展示对象")
public class ExamQuestionManualVo extends ExamQuestionVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "考试ID")
    private Long examId;

    @ApiModelProperty(value = "试题ID")
    private Long questionId;

    @ApiModelProperty(value = "试题分数")
    private Integer score;

}
