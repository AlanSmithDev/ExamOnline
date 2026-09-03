package com.sdut.examonline.paper.vo;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "试卷试题预选答案前端展示对象", description = "试卷试题预选答案前端展示对象")
public class PaperQuestionAnswerVo extends BaseDto {

    @ApiModelProperty(value = "预选答案ID")
    private Long id;

    @ApiModelProperty(value = "预选答案内容")
    private String content;

}
