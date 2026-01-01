package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "保存预选答案请求", description = "保存预选答案请求")
public class QuestionAnswerSaveRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "答案内容", required = true)
    private String content;

    @ApiModelProperty(value = "是否为正确答案", required = true)
    private Boolean isRight;

    @ApiModelProperty(value = "所属题目", required = true)
    private Long questionId;

}
