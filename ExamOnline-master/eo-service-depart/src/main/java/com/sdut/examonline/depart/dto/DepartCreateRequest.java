package com.sdut.examonline.depart.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "创建班级请求", description = "创建班级请求")
public class DepartCreateRequest extends BaseDto {

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @NotBlank(message = "班级名称不能为空")
    @ApiModelProperty(value = "班级名称", required = true)
    private String deptName;

}
