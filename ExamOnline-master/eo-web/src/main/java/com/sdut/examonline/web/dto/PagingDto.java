package com.sdut.examonline.web.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(value = "分页请求封装类", description = "分页请求封装类")
@Data
public class PagingDto extends BaseDto {

    @ApiModelProperty(value = "当前页码", required = true, example = "1")
    private Long pageNum;

    @ApiModelProperty(value = "每页数量", required = true, example = "10")
    private Long pageSize;

}