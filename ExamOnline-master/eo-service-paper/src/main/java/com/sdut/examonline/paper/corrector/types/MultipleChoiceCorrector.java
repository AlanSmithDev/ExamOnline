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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Chen
 * @since 2022/3/14 15:19
 */
@Component
public class MultipleChoiceCorrector implements BaseCorrector {

    @Override
    public Integer getQuestionType() {
        return QuestionType.MULTIPLE_CHOICE;
    }

    @Override
    public Tuple<Boolean, Integer> correct(PaperQuestion paperQuestion, PaperAnswer paperAnswer, QuestionDto question, List<QuestionAnswerDto> correctAnswers) {
        if (paperAnswer == null || question == null || StringUtils.isEmpty(paperAnswer.getContent())) {
            return Tuple.of(false, 0);
        }

        Set<String> answerIds;
        if (StringUtils.isEmpty(paperAnswer.getContent())) {
            return wrongAnswer();
        }
        if (paperAnswer.getContent().contains(",")) {
            answerIds = Arrays.stream(paperAnswer.getContent().split(",")).collect(Collectors.toSet());
        } else {
            answerIds = Collections.singleton(paperAnswer.getContent());
        }

        if (answerIds.size() != correctAnswers.size()) {
            return wrongAnswer();
        }
        for (QuestionAnswerDto correctAnswer : correctAnswers) {
            if (!answerIds.contains(String.valueOf(correctAnswer.getId()))) {
                return wrongAnswer();
            }
        }
        return correctAnswer(paperQuestion.getScore());
    }
}
