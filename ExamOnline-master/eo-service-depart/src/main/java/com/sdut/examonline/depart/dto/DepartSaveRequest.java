package com.sdut.examonline.depart.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "保存班级信息请求", description = "保存班级信息请求")
public class DepartSaveRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "备注", required = true)
    private String remark;

    @NotBlank(message = "班级名称不能为空")
    @ApiModelProperty(value = "班级名称", required = true)
    private String deptName;

}
