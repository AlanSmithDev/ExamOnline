package com.sdut.examonline.user.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户注册请求", description = "用户注册请求")
public class UserRegisterRequest extends BaseDto {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 5, max = 20, message = "用户名长度为5-20之间")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "真实姓名不能为空")
    @Length(min = 2, max = 10, message = "真实姓名长度为2-10之间")
    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @ApiModelProperty(value = "生日", required = true)
    private String birthday;

    @Range(min = 0, max = 2, message = "性别填写错误")
    @ApiModelProperty(value = "性别", required = true)
    private Integer gender;

    @Range(min = 0, max = 1, message = "平台值为0(网页)或1(小程序)")
    @ApiModelProperty(value = "平台")
    private Integer platform = 0;

}
