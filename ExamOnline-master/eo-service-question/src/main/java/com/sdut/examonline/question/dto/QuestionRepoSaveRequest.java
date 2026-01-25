package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "题库信息保存请求", description = "题库信息保存请求")
public class QuestionRepoSaveRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @NotBlank(message = "题库名称不能为空")
    @ApiModelProperty(value = "题库名称", required = true)
    private String name;

    @ApiModelProperty(value = "原密码(原先无密码则填至新密码)", required = true)
    private String password;

    @ApiModelProperty(value = "新密码", required = true)
    private String newPassword;

}
