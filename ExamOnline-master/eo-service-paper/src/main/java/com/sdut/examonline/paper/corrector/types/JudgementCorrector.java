package com.sdut.examonline.paper.corrector.types;

import com.sdut.examonline.common.constant.QuestionType;
import com.sdut.examonline.common.util.Tuple;
import com.sdut.examonline.dubbo.dto.QuestionAnswerDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.paper.corrector.BaseCorrector;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.entity.PaperQuestion;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Chen
 * @since 2022/3/14 15:19
 */
@Component
public class JudgementCorrector implements BaseCorrector {

    @Override
    public Integer getQuestionType() {
        return QuestionType.JUDGEMENT;
    }

    @Override
    public Tuple<Boolean, Integer> correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer, QuestionDto question, List<QuestionAnswerDto> correctAnswers) {
        if (paperAnswer == null || question == null || correctAnswers.size() != 1 || StringUtils.isEmpty(paperAnswer.getContent())) {
            return wrongAnswer();
        }
        QuestionAnswerDto questionAnswer = correctAnswers.get(0);
        boolean right = "true".equalsIgnoreCase(paperAnswer.getContent());
        if (!Objects.equals(questionAnswer.getIsRight(), right)) {
            return wrongAnswer();
        }
        return correctAnswer(paperQuestion.getScore());
    }
}
