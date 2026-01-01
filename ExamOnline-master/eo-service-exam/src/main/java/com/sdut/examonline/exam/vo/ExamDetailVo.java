package com.sdut.examonline.exam.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "考试详情前端展示对象", description = "考试详情前端展示对象")
public class ExamDetailVo extends ExamVo {

    @ApiModelProperty(value = "组卷试题列表")
    private List<ExamQuestionVo> questions;

}
