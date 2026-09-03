package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.vo.BaseVo;
import lombok.Data;

@Data
public class PaperRecordDto extends BaseVo {

    private Long id;

    private Long userId;

    private Integer score;

    private Integer state;

}
