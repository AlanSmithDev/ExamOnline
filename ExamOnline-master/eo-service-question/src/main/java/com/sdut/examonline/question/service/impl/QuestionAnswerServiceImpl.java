package com.sdut.examonline.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.JsonUtils;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.question.dto.QuestionAnswerCreateRequest;
import com.sdut.examonline.question.dto.QuestionAnswerSaveRequest;
import com.sdut.examonline.question.entity.QuestionAnswer;
import com.sdut.examonline.question.mapper.QuestionAnswerMapper;
import com.sdut.examonline.question.service.QuestionAnswerService;
import com.sdut.examonline.question.service.QuestionService;
import com.sdut.examonline.question.vo.QuestionAnswerVo;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionAnswerServiceImpl extends ServiceImpl<QuestionAnswerMapper, QuestionAnswer> implements QuestionAnswerService {

    @Autowired
    CacheUtils cacheUtils;

    @Autowired
    QuestionService questionService;

    @Override
    public List<QuestionAnswerVo> getAnswersByQuestionId(Long questionId) {
        return MapperUtils.mapList(getAnswersByQuestionIdInner(questionId), QuestionAnswerVo.class);
    }

    @Override
    public List<QuestionAnswer> getAnswersByQuestionIdInner(Long questionId) {
        return getQuestionAnswerListInner(questionId);
    }

    @Override
    public QuestionAnswerVo addAnswer(QuestionAnswerCreateRequest createRequest) {
        QuestionAnswer answer = new QuestionAnswer();
        MapperUtils.map(createRequest, answer);
        answer.setId(SnowFlakeIdWorker.nextId());

        Date now = new Date();
        answer.setUpdateTime(now);
        answer.setCreateTime(now);

        super.save(answer);

        questionService.removeQuestionCache(answer.getQuestionId());

        QuestionAnswerVo questionAnswerVo = new QuestionAnswerVo();
        MapperUtils.map(answer, questionAnswerVo);

        return questionAnswerVo;
    }

    @Override
    public QuestionAnswerVo saveAnswer(QuestionAnswerSaveRequest saveRequest) {
        QuestionAnswer answer = getQuestionAnswerInner(saveRequest.getId());
        Assert.notNull(answer, ErrorCode.QUESTION_ANSWER_NOT_FOUND);

        MapperUtils.map(saveRequest, answer);
        answer.setUpdateTime(new Date());

        super.updateById(answer);

        removeAnswerCache(answer.getQuestionId(), answer.getId());

        QuestionAnswerVo questionAnswerVo = new QuestionAnswerVo();
        MapperUtils.map(answer, questionAnswerVo);

        return questionAnswerVo;
    }

    @Override
    public Boolean removeAnswer(Long answerId) {
        LambdaQueryWrapper<QuestionAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionAnswer::getId, answerId);
        boolean result = super.remove(wrapper);

        removeAnswerCache(null, answerId);

        return result;
    }

    private List<QuestionAnswer> getQuestionAnswerListInner(Long questionId) {
        String str = cacheUtils.getString(getCacheKeyAnswerList(questionId), 10, TimeUnit.MINUTES);
        if (cacheUtils.isNullString(str)) {
            return Collections.emptyList();
        }
        if (StringUtils.isNotEmpty(str)) {
            try {
                return JsonUtils.from(str, ArrayList.class, QuestionAnswer.class);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Collections.emptyList();
            }
        }
        QueryWrapper<QuestionAnswer> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(QuestionAnswer::getQuestionId, questionId);
        List<QuestionAnswer> list = super.list(wrapper);
        try {
            cacheUtils.setString(getCacheKeyAnswerList(questionId), JsonUtils.toJson(list), 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return list;
    }

    private QuestionAnswer getQuestionAnswerInner(Long answerId) {
        Object value = cacheUtils.get(getCacheKeyAnswer(answerId), QuestionAnswer.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.QUESTION_ANSWER_NOT_FOUND);
        }
        if (value != null) {
            return (QuestionAnswer) value;
        }
        QueryWrapper<QuestionAnswer> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(QuestionAnswer::getId, answerId);
        QuestionAnswer questionAnswer = super.getOne(wrapper, false);
        if (questionAnswer != null) {
            cacheUtils.set(getCacheKeyAnswer(questionAnswer.getId()), questionAnswer, 10, TimeUnit.MINUTES);
        }
        return questionAnswer;
    }

    private void removeAnswerCache(Long questionId, Long answerId) {
        if (questionId != null && questionId > 0) {
            questionService.removeQuestionCache(questionId);
            cacheUtils.delete(getCacheKeyAnswerList(questionId));
        }
        cacheUtils.delete(getCacheKeyAnswer(answerId));
    }

    private String getCacheKeyAnswer(Long answerId) {
        return "quan:id:" + answerId;
    }

    private String getCacheKeyAnswerList(Long questionId) {
        return "quan:ls:" + questionId;
    }

}
