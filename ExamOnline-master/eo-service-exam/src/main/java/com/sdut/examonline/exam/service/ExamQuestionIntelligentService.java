package com.sdut.examonline.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.exam.dto.ExamQuestionIntelligentAddRequest;
import com.sdut.examonline.exam.dto.ExamQuestionIntelligentSaveRequest;
import com.sdut.examonline.exam.dto.ExamQuestionSetIntelligentAddRequest;
import com.sdut.examonline.exam.entity.ExamQuestionIntelligent;
import com.sdut.examonline.exam.vo.ExamQuestionIntelligentVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;

public interface ExamQuestionIntelligentService extends IService<ExamQuestionIntelligent> {

    /**
     * 查询试卷所有智能组卷试题
     *
     * @param examId 考试ID
     */
    List<ExamQuestionIntelligentVo> allIntelligents(Long examId);

    /**
     * 查询试卷所有智能组卷试题(内部调用)
     *
     * @param examId 考试ID
     */
    List<ExamQuestionIntelligent> allIntelligentsInner(Long examId);

    /**
     * 分页查询试卷所有智能组卷试题
     *
     * @param pagingParamDto 带参分页请求
     */
    PagingVo<ExamQuestionIntelligentVo> pageIntelligents(PagingParamDto<String> pagingParamDto);

    /**
     * 新增智能组卷试题
     *
     * @param addRequest 添加请求
     */
    ExamQuestionIntelligentVo addIntelligent(ExamQuestionIntelligentAddRequest addRequest);

    /**
     * 新增智能组卷试题列表
     *
     * @param addRequest 添加请求
     */
    List<ExamQuestionIntelligentVo> addIntelligentSet(ExamQuestionSetIntelligentAddRequest addRequest);

    /**
     * 保存智能组卷试题
     *
     * @param saveRequest 保存请求
     */
    ExamQuestionIntelligentVo saveIntelligent(ExamQuestionIntelligentSaveRequest saveRequest);

    /**
     * 删除智能组卷试题
     *
     * @param intelligentId 试题ID
     */
    Boolean deleteIntelligent(String intelligentId);

}
