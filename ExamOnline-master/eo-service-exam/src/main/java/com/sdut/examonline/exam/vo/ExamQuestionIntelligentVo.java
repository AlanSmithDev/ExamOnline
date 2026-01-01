package com.sdut.examonline.exam.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "手动组卷试题前端展示对象", description = "手动组卷试题前端展示对象")
public class ExamQuestionIntelligentVo extends ExamQuestionVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "考试ID")
    private Long examId;

    @ApiModelProperty(value = "题库ID")
    private Long repoId;

    @ApiModelProperty(value = "试题类型")
    private Integer questionType;

    @ApiModelProperty(value = "抽取数量")
    private Integer amount;

    @ApiModelProperty(value = "试题分数")
    private Integer score;

}
