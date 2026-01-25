package com.sdut.examonline.paper.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "试卷记录详情前端展示对象", description = "试卷记录详情前端展示对象")
public class PaperRecordDetailVo extends BaseVo {

    @ApiModelProperty(value = "试卷记录ID")
    private Long id;

    @ApiModelProperty(value = "试卷")
    private PaperVo paper;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "分数")
    private Integer score;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始答题时间", example = "2022-03-10 00:00:00")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "交卷时间", example = "2022-03-10 00:00:00")
    private Date endTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间", example = "2022-03-10 00:00:00")
    private Date updateTime;

}
