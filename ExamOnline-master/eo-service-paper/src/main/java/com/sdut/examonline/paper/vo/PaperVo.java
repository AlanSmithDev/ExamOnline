package com.sdut.examonline.paper.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "试卷前端展示对象", description = "试卷前端展示对象")
public class PaperVo extends BaseVo {

    @ApiModelProperty(value = "试卷ID")
    private Long id;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "试题列表")
    private List<PaperQuestionVo> questions;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间", example = "2022-03-10 00:00:00")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间", example = "2022-03-10 00:00:00")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", example = "2022-03-10 00:00:00")
    private Date updateTime;

}
