package com.sdut.examonline.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "题目预选答案前端展示对象", description = "题目预选答案前端展示对象")
public class QuestionAnswerVo extends BaseDto {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "答案内容")
    private String content;

    @ApiModelProperty(value = "是否为正确答案")
    private Boolean isRight;

    @ApiModelProperty(value = "所属题目")
    private Long questionId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", example = "2022-03-10 00:00:00")
    private Date updateTime;

}
