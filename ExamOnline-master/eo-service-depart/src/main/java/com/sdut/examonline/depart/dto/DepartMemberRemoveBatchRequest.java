package com.sdut.examonline.depart.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

@Data
@ApiModel(value = "批量删除班级成员请求", description = "批量删除班级成员请求")
public class DepartMemberRemoveBatchRequest extends BaseDto {

    @ApiModelProperty(value = "班级ID", required = true)
    private Long id;

    @ApiModelProperty(value = "用户ID集", required = true)
    private Set<Long> userIds;

}
