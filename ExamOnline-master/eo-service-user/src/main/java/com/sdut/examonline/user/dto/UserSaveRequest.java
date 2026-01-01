package com.sdut.examonline.user.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "保存用户资料请求", description = "保存用户资料请求")
public class UserSaveRequest extends BaseDto {

    @NotBlank(message = "真实姓名不能为空")
    @Length(min = 2, max = 10, message = "真实姓名长度为2-10之间")
    @ApiModelProperty(value = "真实姓名", required = true)
    private String realName;

    @ApiModelProperty(value = "角色", required = true)
    private String role;

    @ApiModelProperty(value = "生日", required = true)
    private String birthday;

    @Range(min = 0, max = 2, message = "性别填写错误")
    @ApiModelProperty(value = "性别", required = true)
    private Integer gender;

}
