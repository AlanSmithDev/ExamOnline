package com.sdut.examonline.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "用户前端展示对象", description = "用户前端展示对象")
public class UserVo extends BaseVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "真实姓名")
    private String realName;

    @ApiModelProperty(value = "角色")
    private String role;

    @ApiModelProperty(value = "头像")
    private String avatar;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @ApiModelProperty(value = "生日", example = "2022-03-10")
    private String birthday;

    @ApiModelProperty(value = "性别")
    private Integer gender;

}
