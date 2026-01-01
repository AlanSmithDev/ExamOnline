package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "题库创建请求", description = "题库创建请求")
public class QuestionRepoCreateRequest extends BaseDto {

    @NotBlank(message = "题库名称不能为空")
    @ApiModelProperty(value = "题库名称", required = true)
    private String name;

    @ApiModelProperty(value = "题库密码", required = true)
    private String password;

    @ApiModelProperty(value = "题库类型", required = true)
    private Integer type = 0;

}
