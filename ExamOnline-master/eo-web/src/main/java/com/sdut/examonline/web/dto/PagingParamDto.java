package com.sdut.examonline.web.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "分页参数请求封装类", description = "分页参数请求封装类")
@Data
public class PagingParamDto<T> extends PagingDto {

    @ApiModelProperty(value = "查询参数", required = true)
    private T param;

}