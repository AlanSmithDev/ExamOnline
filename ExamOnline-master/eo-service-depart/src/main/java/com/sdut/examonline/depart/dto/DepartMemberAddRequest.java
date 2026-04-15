package com.sdut.examonline.depart.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "添加班级成员请求", description = "添加班级成员请求")
public class DepartMemberAddRequest extends BaseDto {

    @ApiModelProperty(value = "班级ID", required = true)
    private Long id;

    @ApiModelProperty(value = "用户ID", required = true)
    private Long userId;

}
