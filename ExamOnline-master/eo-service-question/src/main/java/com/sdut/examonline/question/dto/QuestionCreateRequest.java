package com.sdut.examonline.question.dto;

import com.sdut.examonline.common.dto.BaseDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
@ApiModel(value = "题目创建请求", description = "题目创建请求")
public class QuestionCreateRequest extends BaseDto {

    @ApiModelProperty(value = "题目类型", required = true)
    private Integer type;

    @Range(min = 1, max = 5, message = "题目难度必须为0到5之间")
    @ApiModelProperty(value = "题目难度", required = true)
    private Integer difficulty;

    @ApiModelProperty(value = "题目内容", required = true)
    private String content;

    @ApiModelProperty(value = "音频文件", required = true)
    private String voiceFile;

    @ApiModelProperty(value = "所属题库", required = true)
    private Long repoId;

    @ApiModelProperty(value = "父题目ID", required = true)
    private Long parentId;

    @ApiModelProperty(value = "题目解析", required = true)
    private String analysis;

    @ApiModelProperty(value = "题目解析视频", required = true)
    private String analysisVideo;

}
