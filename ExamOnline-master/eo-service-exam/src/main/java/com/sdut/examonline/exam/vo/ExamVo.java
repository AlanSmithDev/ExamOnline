package com.sdut.examonline.exam.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "考试前端展示对象", description = "考试前端展示对象")
public class ExamVo extends BaseVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "组卷方式(0手动组卷,1智能组卷)")
    private Integer joinType;

    @ApiModelProperty(value = "试卷ID")
    private Long paperId;

    @ApiModelProperty(value = "考试标题")
    private String title;

    @ApiModelProperty(value = "考试内容")
    private String content;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "总分")
    private Integer totalScore;

    @ApiModelProperty(value = "是否限时")
    private Boolean timeLimit;

    @ApiModelProperty(value = "是否人脸识别")
    private Boolean faceCheck;

    @ApiModelProperty(value = "限时进入(分钟,0为不限制)")
    private Integer joinLimit;

    @ApiModelProperty(value = "限时提交(分钟,0为不限制)")
    private Integer submitLimit;

    @ApiModelProperty(value = "考试时长(分钟)")
    private Integer duration;

    @ApiModelProperty(value = "参考班级ID列表")
    private List<Long> departIdList;

    @ApiModelProperty(value = "创建者")
    private Long creator;

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
