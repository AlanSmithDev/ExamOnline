package com.sdut.examonline.paper.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "试卷试题前端展示对象", description = "试卷试题前端展示对象")
public class PaperQuestionVo extends BaseVo {

    @ApiModelProperty(value = "试题前ID")
    private Long id;

    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    @ApiModelProperty(value = "试题ID")
    private Long questionId;

    @ApiModelProperty(value = "试题分数")
    private Integer score;

    @ApiModelProperty(value = "题目类型")
    private Integer type;

    @ApiModelProperty(value = "题目内容")
    private String content;

}
