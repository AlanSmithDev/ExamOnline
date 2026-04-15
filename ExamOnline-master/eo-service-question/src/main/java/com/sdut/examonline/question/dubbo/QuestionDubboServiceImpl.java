package com.sdut.examonline.question.dubbo;

import com.sdut.examonline.dubbo.client.QuestionDubboService;
import com.sdut.examonline.dubbo.dto.QuestionAnswerDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.dubbo.dto.QuestionIntelligentDto;
import com.sdut.examonline.question.service.QuestionAnswerService;
import com.sdut.examonline.question.service.QuestionRepoService;
import com.sdut.examonline.question.service.QuestionService;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 11:34
 */
@Service(retries = 0)
public class QuestionDubboServiceImpl implements QuestionDubboService {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionRepoService questionRepoService;

    @Autowired
    QuestionAnswerService questionAnswerService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("question_count", questionService.count());
            put("question_repo_count", questionRepoService.count());
        }};
    }

    @Override
    public QuestionDto getQuestionById(Long questionId) {
        return MapperUtils.map(questionService.detail(questionId), QuestionDto.class);
    }

    @Override
    public QuestionDto getQuestionConciseById(Long questionId) {
        return MapperUtils.map(questionService.getQuestionByIdInner(questionId), QuestionDto.class);
    }

    @Override
    public List<QuestionAnswerDto> getAnswersByQuestionId(Long questionId) {
        return MapperUtils.mapList(questionAnswerService.getAnswersByQuestionIdInner(questionId), QuestionAnswerDto.class);
    }

    @Override
    public List<QuestionDto> randomGet(QuestionIntelligentDto setting) {
        return questionService.randomGet(setting);
    }

}
