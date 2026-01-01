package com.sdut.examonline.paper.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "试卷试题前端展示对象", description = "试卷试题前端展示对象")
public class PaperQuestionDetailVo extends BaseVo {

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

    @ApiModelProperty(value = "音频文件")
    private String voiceFile;

    @ApiModelProperty(value = "预选答案列表")
    private List<PaperQuestionAnswerVo> answers;

    @ApiModelProperty(value = "子题目列表")
    private List<PaperQuestionDetailVo> subQuestions;

}
