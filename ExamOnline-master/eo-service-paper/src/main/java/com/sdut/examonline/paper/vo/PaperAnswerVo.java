package com.sdut.examonline.paper.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "答题记录前端展示对象", description = "答题记录前端展示对象")
public class PaperAnswerVo extends BaseVo {

    @ApiModelProperty(value = "答题记录ID")
    private Long id;

    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    @ApiModelProperty(value = "试题ID")
    private Long questionId;

    @ApiModelProperty(value = "提交者ID")
    private Long userId;

    @ApiModelProperty(value = "所答内容")
    private String content;

    @ApiModelProperty(value = "是否答对")
    private Boolean isRight;

    @ApiModelProperty(value = "得分")
    private Integer score;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", example = "2022-03-10 00:00:00")
    private Date updateTime;

}
