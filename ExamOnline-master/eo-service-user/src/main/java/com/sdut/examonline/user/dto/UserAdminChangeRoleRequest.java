package com.sdut.examonline.user.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "管理员修改用户身份请求", description = "管理员修改用户身份请求")
public class UserAdminChangeRoleRequest extends BaseDto {

    @NotNull(message = "用户ID不能为空")
    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

    @NotBlank(message = "身份不能为空")
    @ApiModelProperty(value = "身份", required = true)
    private String role;

}
