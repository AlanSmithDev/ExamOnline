package com.sdut.examonline.paper.corrector;

import com.sdut.examonline.common.util.Tuple;
import com.sdut.examonline.dubbo.dto.QuestionAnswerDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.entity.PaperQuestion;

import java.util.List;

/**
 * @author Chen
 * @since 2022/3/13 18:05
 */
public interface BaseCorrector {

    /**
     * 所批改的试题类型
     */
    Integer getQuestionType();

    /**
     * 批改试题
     *
     * @param paperQuestion  试卷题目
     * @param paperAnswer    答题记录
     * @param question       题目
     * @param correctAnswers 题目的正确答案
     * @return 二元组 是否正确, 获得分数
     */
    Tuple<Boolean, Integer> correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer, QuestionDto question, List<QuestionAnswerDto> correctAnswers);

    default Tuple<Boolean, Integer> wrongAnswer() {
        return ofResult(false, 0);
    }

    default Tuple<Boolean, Integer> correctAnswer(Integer score) {
        return ofResult(true, score);
    }

    default Tuple<Boolean, Integer> ofResult(Boolean correct, Integer score) {
        return Tuple.of(correct, score);
    }

}
