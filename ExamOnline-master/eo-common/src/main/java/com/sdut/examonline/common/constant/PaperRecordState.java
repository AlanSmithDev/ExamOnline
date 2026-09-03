package com.sdut.examonline.common.constant;

/**
 * @author Chen
 * @since 2022/4/4 20:43
 */
public class PaperRecordState {

    /**
     * 已准备
     */
    public static final Integer PREPARED = 0;

    /**
     * 答题中
     */
    public static final Integer ANSWRING = 1;

    /**
     * 已提交
     */
    public static final Integer SUBMITTED = 2;

    /**
     * 批改完毕
     * <p>
     * 用于提前提交试卷，单独提前批改以减少系统负担
     */
    public static final Integer CORRECTED = 3;

}
