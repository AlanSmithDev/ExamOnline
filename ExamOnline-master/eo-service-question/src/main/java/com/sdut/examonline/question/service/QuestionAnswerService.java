package com.sdut.examonline.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.question.dto.QuestionAnswerCreateRequest;
import com.sdut.examonline.question.dto.QuestionAnswerSaveRequest;
import com.sdut.examonline.question.entity.QuestionAnswer;
import com.sdut.examonline.question.vo.QuestionAnswerVo;

import java.util.List;

public interface QuestionAnswerService extends IService<QuestionAnswer> {

    /**
     * 获取题目预选答案
     *
     * @param questionId 题目ID
     */
    List<QuestionAnswerVo> getAnswersByQuestionId(Long questionId);

    /**
     * 获取题目预选答案(内部调用)
     *
     * @param questionId 题目ID
     */
    List<QuestionAnswer> getAnswersByQuestionIdInner(Long questionId);

    /**
     * 新增预选答案
     *
     * @param createRequest 创建请求
     */
    QuestionAnswerVo addAnswer(QuestionAnswerCreateRequest createRequest);

    /**
     * 保存预选答案
     *
     * @param saveRequest 保存请求
     */
    QuestionAnswerVo saveAnswer(QuestionAnswerSaveRequest saveRequest);

    /**
     * 移除预选答案
     *
     * @param answerId 预选答案ID
     */
    Boolean removeAnswer(Long answerId);
}
