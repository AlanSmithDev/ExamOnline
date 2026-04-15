package com.sdut.examonline.paper.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "批改答题记录请求", description = "批改答题记录请求")
public class PaperAnswerCorrectRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "是否答对", required = true)
    private Boolean isRight;

    @ApiModelProperty(value = "得分", required = true)
    private Integer score;

}
