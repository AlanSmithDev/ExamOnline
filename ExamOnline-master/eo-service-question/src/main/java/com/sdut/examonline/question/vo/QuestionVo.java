package com.sdut.examonline.question.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "题目前端展示对象", description = "题目前端展示对象")
public class QuestionVo extends BaseDto {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "题目类型")
    private Integer type;

    @ApiModelProperty(value = "题目难度")
    private Integer difficulty;

    @ApiModelProperty(value = "题目内容")
    private String content;

    @ApiModelProperty(value = "音频文件")
    private String voiceFile;

    @ApiModelProperty(value = "所属题库")
    private Long repoId;

    @ApiModelProperty(value = "创建者")
    private Long creator;

    @ApiModelProperty(value = "题目解析")
    private String analysis;

    @ApiModelProperty(value = "题目解析视频")
    private String analysisVideo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", example = "2022-03-10 00:00:00")
    private Date updateTime;

    @ApiModelProperty(value = "题目预选答案")
    private List<QuestionAnswerVo> answers;

    @ApiModelProperty(value = "子题目列表")
    private List<QuestionVo> subQuestions;

}
