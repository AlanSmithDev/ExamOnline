package com.sdut.examonline.exam.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "考试分析前端展示对象", description = "考试分析前端展示对象")
public class ExamAnalysisVo extends BaseVo {

    @ApiModelProperty(value = "参考数")
    private String name;

    @ApiModelProperty(value = "参考数")
    private Integer attendNum;

    @ApiModelProperty(value = "缺考数")
    private Integer absentNum;

    @ApiModelProperty(value = "最低分")
    private Integer lowestScore;

    @ApiModelProperty(value = "最高分")
    private Integer highestScore;

    @ApiModelProperty(value = "平均分")
    private String averageScore;

    @ApiModelProperty(value = "及格率")
    private String passRate;

    @ApiModelProperty(value = "班级成绩分析")
    private List<ExamAnalysisVo> departAnalysis;

}
