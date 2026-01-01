package com.sdut.examonline.paper.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Chen
 * @since 2022/4/7 8:56
 */
@Data
public class PoiQuestionSetVo {

    private String title;

    private List<PoiQuestionVo> questions;

}
