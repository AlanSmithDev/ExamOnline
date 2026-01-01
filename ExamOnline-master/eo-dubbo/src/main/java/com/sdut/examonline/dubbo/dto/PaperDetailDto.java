package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;

import java.util.Date;
import java.util.List;

/**
 * @author Chen
 * @since 2022/3/19 8:53
 */
public class PaperDetailDto extends BaseDto {

    private Long id;

    private Long userId;

    private Long examId;

    private Integer score;

    private Integer state;

    private List<PaperQuestionDto> questions;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

}
