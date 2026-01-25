package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "题库密码验证请求", description = "题库密码验证请求")
public class QuestionRepoVerifyRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "题库密码", required = true)
    private String password;

}
