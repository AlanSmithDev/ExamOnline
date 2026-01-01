package com.sdut.examonline.user.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "令牌前端展示对象", description = "令牌前端展示对象")
public class UserLoginVo extends UserVo {

    @ApiModelProperty(value = "令牌")
    private String token;

}
