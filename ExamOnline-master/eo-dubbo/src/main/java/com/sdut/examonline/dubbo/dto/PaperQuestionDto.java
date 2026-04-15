package com.sdut.examonline.dubbo.dto;

import com.sdut.examonline.common.dto.BaseDto;

/**
 * @author Chen
 * @since 2022/3/19 8:54
 */
public class PaperQuestionDto extends BaseDto {

    private Long id;

    private Long paperId;

    private Long questionId;

    private Integer score;

    private Integer type;

    private String content;

}
