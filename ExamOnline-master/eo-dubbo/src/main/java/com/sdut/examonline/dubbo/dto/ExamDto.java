package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.Date;

@Data
public class ExamDto extends BaseDto {

    private Long id;

    private Integer joinType;

    private String title;

    private String content;

    private Integer state;

    private Integer totalScore;

    private Boolean timeLimit;

    private Integer joinLimit;

    private Integer submitLimit;

    private Long departId;

    private Long creator;

    private Date startTime;

    private Date endTime;

    private Date createTime;

    private Date updateTime;

}