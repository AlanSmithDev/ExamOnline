package com.sdut.examonline.paper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.paper.dto.PaperAnswerCorrectRequest;
import com.sdut.examonline.paper.dto.PaperAnswerSaveRequest;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.vo.PaperAnswerVo;
import com.sdut.examonline.paper.vo.PaperCorrectInfoVo;

import java.util.List;

public interface PaperAnswerService extends IService<PaperAnswer> {

    /**
     * 保存答题记录
     *
     * @param saveRequest 保存请求
     */
    PaperAnswerVo save(PaperAnswerSaveRequest saveRequest);

    /**
     * 获取自己的考试答题记录
     *
     * @param paperId 试卷ID
     */
    List<PaperAnswerVo> myAnswers(Long paperId);

    /**
     * 获取学生的考试答题记录
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     */
    List<PaperAnswerVo> answers(Long paperId, Long userId);

    /**
     * 教师手动批改试卷
     *
     * @param request 批改请求
     */
    PaperAnswerVo correct(PaperAnswerCorrectRequest request);

    /**
     * 获取下一个需要批改的答题记录
     *
     * @param paperId    试卷ID
     * @param questionId 试题ID
     */
    PaperAnswerVo nextAnswer(Long paperId, Long questionId);

    /**
     * 获取试卷的批改详情
     *
     * @param paperId 试卷ID
     */
    PaperCorrectInfoVo getCorrectInfo(Long paperId);

}
