package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.dubbo.dto.QuestionAnswerDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.dubbo.dto.QuestionIntelligentDto;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:26
 */
public interface QuestionDubboService {

    Map<String, Object> getAdminDashboard();

    QuestionDto getQuestionById(Long questionId);

    QuestionDto getQuestionConciseById(Long questionId);

    List<QuestionAnswerDto> getAnswersByQuestionId(Long questionId);

    List<QuestionDto> randomGet(QuestionIntelligentDto setting);

}
