package com.sdut.examonline.paper.corrector;

import com.google.common.collect.Maps;
import com.sdut.examonline.common.util.Tuple;
import com.sdut.examonline.dubbo.dto.QuestionAnswerDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.entity.PaperQuestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/14 14:54
 */
@Component
public class Corrector {

    private final Map<Integer, BaseCorrector> correctorMap = Maps.newHashMapWithExpectedSize(3);

    @Autowired
    ApplicationContext context;

    @PostConstruct
    public void init() {
        for (BaseCorrector corrector : context.getBeansOfType(BaseCorrector.class).values()) {
            correctorMap.put(corrector.getQuestionType(), corrector);
        }
    }

    /**
     * 批改题目
     *
     * @param paperQuestion  试卷题目
     * @param paperAnswer    答题记录
     * @param question       题目
     * @param correctAnswers 题目的正确答案
     * @return 是否批改
     * 注意！！！
     * 与BaseCorrector.correct不同
     */
    public boolean correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer, QuestionDto question, List<QuestionAnswerDto> correctAnswers) {
        BaseCorrector corrector = correctorMap.get(question.getType());
        //为空则需人工批改
        if (corrector == null) {
            return false;
        }
        Tuple<Boolean, Integer> result = corrector.correct(paperQuestion, paperAnswer, question, correctAnswers);
        paperAnswer.setCorrector(0L);
        paperAnswer.setIsRight(result.getA());
        paperAnswer.setScore(result.getB());
        return true;
    }

}
