package com.sdut.examonline.user.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "用户登录请求", description = "用户登录请求")
public class UserLoginRequest extends BaseDto {

    @NotBlank(message = "用户名不能为空")
    @Length(min = 5, max = 20, message = "用户名长度为5-20之间")
    @ApiModelProperty(value = "用户名", required = true)
    private String username;

    @NotBlank(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;

    @Range(min = 0, max = 1, message = "平台值为0(网页)或1(小程序)")
    @ApiModelProperty(value = "平台")
    private Integer platform = 0;

}
