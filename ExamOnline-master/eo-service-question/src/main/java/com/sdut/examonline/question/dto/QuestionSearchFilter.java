package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "题目搜索请求", description = "题目搜索请求")
public class QuestionSearchFilter extends BaseDto {

    @ApiModelProperty(value = "所查题库ID(必填)", required = true)
    private Long repoId;

    @ApiModelProperty(value = "题目内容(不使用则取null)", required = true)
    private String content;

    @ApiModelProperty(value = "题目类型", required = true)
    private List<Integer> types;

    @ApiModelProperty(value = "题目难度", required = true)
    private List<Integer> difficulties;

}
