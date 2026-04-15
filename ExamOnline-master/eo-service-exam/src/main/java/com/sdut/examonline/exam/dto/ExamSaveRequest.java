package com.sdut.examonline.exam.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Date;
import java.util.List;

@Data
@ApiModel(value = "保存考试请求", description = "保存考试请求")
public class ExamSaveRequest extends BaseDto {

    @ApiModelProperty(value = "考试ID", required = true)
    private Long id;

    @Range(min = 0, max = 1, message = "组卷方式错误:0手动组卷,1智能组卷")
    @ApiModelProperty(value = "组卷方式(0手动组卷,1智能组卷)", required = true)
    private Integer joinType;

    @NotBlank(message = "考试标题不能为空")
    @ApiModelProperty(value = "考试标题", required = true)
    private String title;

    @ApiModelProperty(value = "考试内容", required = true)
    private String content;

    @ApiModelProperty(value = "参考班级列表", required = true)
    private List<Long> departIdList;

    @Positive(message = "及格分必须大于0")
    @ApiModelProperty(value = "总分", required = true)
    private Integer totalScore;

    @ApiModelProperty(value = "是否限时", required = true)
    private Boolean timeLimit;

    @PositiveOrZero(message = "限时进入(分钟)必须大于等于0(0为不限制)")
    @ApiModelProperty(value = "限时进入(分钟,0为不限制)", required = true)
    private Integer joinLimit;

    @PositiveOrZero(message = "限时提交(分钟)必须大于等于0(0为不限制)")
    @ApiModelProperty(value = "限时提交(分钟,0为不限制)", required = true)
    private Integer submitLimit;

    @Positive(message = "考试时长(分钟)必须大于0")
    @ApiModelProperty(value = "考试时长", required = true)
    private Integer duration;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间", required = true, example = "2022-03-10 00:00:00")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间", required = true, example = "2022-03-10 00:00:00")
    private Date endTime;

}
