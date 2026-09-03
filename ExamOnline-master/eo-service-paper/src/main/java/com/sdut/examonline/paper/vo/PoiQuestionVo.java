package com.sdut.examonline.paper.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Chen
 * @since 2022/4/7 8:57
 */
@Data
public class PoiQuestionVo {

    private String num;

    private Integer score;

    private String content;

    private String answers;

    private List<PoiQuestionVo> subQuestions;

}
