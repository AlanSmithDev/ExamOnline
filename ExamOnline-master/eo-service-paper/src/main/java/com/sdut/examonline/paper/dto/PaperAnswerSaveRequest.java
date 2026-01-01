package com.sdut.examonline.paper.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "保存答题记录请求", description = "保存答题记录请求")
public class PaperAnswerSaveRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "所答内容", required = true)
    private String content;

}
