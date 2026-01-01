package com.sdut.examonline.depart.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "批量添加班级成员请求", description = "批量添加班级成员请求")
public class DepartMemberAddBatchRequest extends BaseDto {

    @ApiModelProperty(value = "班级ID", required = true)
    private Long id;

    @ApiModelProperty(value = "用户ID集", required = true)
    private List<Long> userIds;

}
