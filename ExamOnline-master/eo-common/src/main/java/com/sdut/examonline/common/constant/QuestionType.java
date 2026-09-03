package com.sdut.examonline.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/14 13:10
 */
public class QuestionType {

    /**
     * 单选题
     */
    public static final Integer SINGLE_CHOICE = 0;

    /**
     * 多选题
     */
    public static final Integer MULTIPLE_CHOICE = 1;

    /**
     * 判断题
     */
    public static final Integer JUDGEMENT = 2;

    /**
     * 填空题
     * 手动批改
     */
    public static final Integer COMPLETION = 3;

    /**
     * 名词解释题
     * 手动批改
     */
    public static final Integer NOUN_INTERPRETATION = 4;

    /**
     * 完形填空题(单选题集)
     */
    public static final Integer CLOZE = 5;

    /**
     * 阅读理解题(单选题集)
     */
    public static final Integer READING_COMPREHENSION = 6;

    /**
     * 听力题(单选题集)
     */
    public static final Integer LISTENING = 7;

    /**
     * 简答题
     * 手动批改
     */
    public static final Integer SHORT_ANSWER = 8;

    private static final Map<Integer, String> NAME_TABLE = new HashMap<Integer, String>() {{
        put(SINGLE_CHOICE, "单选");
        put(MULTIPLE_CHOICE, "多选");
        put(JUDGEMENT, "判断");
        put(COMPLETION, "填空");
        put(NOUN_INTERPRETATION, "名词解释");
        put(CLOZE, "完形填空");
        put(READING_COMPREHENSION, "阅读理解");
        put(LISTENING, "听力");
        put(SHORT_ANSWER, "简答");
    }};

    public static String getName(Integer type) {
        return NAME_TABLE.getOrDefault(type, null);
    }

    public static boolean hasSubQuestions(Integer type) {
        return (type != null) && (type.equals(CLOZE) || type.equals(READING_COMPREHENSION) || type.equals(LISTENING));
    }

    public static boolean isManualCorrect(Integer type) {
        return (type != null) && (type.equals(COMPLETION) || type.equals(NOUN_INTERPRETATION) || type.equals(SHORT_ANSWER));
    }

}
