package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@ApiModel(value = "题目信息保存请求", description = "题目信息保存请求")
public class QuestionSaveRequest extends BaseDto {

    @ApiModelProperty(value = "ID", required = true)
    private Long id;

    @ApiModelProperty(value = "题目类型", required = true)
    private Integer type;

    @Range(min = 1, max = 5, message = "题目难度必须为0到5之间")
    @ApiModelProperty(value = "题目难度", required = true)
    private Integer difficulty;

    @ApiModelProperty(value = "题目内容", required = true)
    private String content;

    @ApiModelProperty(value = "音频文件", required = true)
    private String voiceFile;

    @ApiModelProperty(value = "题目解析", required = true)
    private String analysis;

    @ApiModelProperty(value = "题目解析视频", required = true)
    private String analysisVideo;

}
