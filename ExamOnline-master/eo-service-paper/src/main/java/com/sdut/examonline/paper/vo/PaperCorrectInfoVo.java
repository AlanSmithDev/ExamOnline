package com.sdut.examonline.paper.vo;

import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "试卷批改信息前端展示对象", description = "试卷批改信息前端展示对象")
public class PaperCorrectInfoVo extends BaseVo {

    @ApiModelProperty(value = "题目列表")
    private List<CorrectInfo> list;

    @Data
    public static class CorrectInfo {

        @ApiModelProperty(value = "题目ID")
        private Long questionId;

        @ApiModelProperty(value = "未批改数")
        private Integer uncorrected;

        @ApiModelProperty(value = "已经批改数")
        private Integer corrected;

    }

}
