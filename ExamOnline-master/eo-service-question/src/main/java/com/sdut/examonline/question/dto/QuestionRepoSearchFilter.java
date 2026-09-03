package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "题库搜索请求", description = "题库搜索请求")
public class QuestionRepoSearchFilter extends BaseDto {

    @ApiModelProperty(value = "题库名称(不使用则取null)", required = true)
    private String name;

    @ApiModelProperty(value = "题库类型(不使用则取-1)", required = true)
    private Integer type = -1;

}
