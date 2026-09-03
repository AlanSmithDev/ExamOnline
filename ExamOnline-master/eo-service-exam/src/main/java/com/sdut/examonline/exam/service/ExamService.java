package com.sdut.examonline.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.exam.dto.ExamCreateRequest;
import com.sdut.examonline.exam.dto.ExamSaveRequest;
import com.sdut.examonline.exam.entity.Exam;
import com.sdut.examonline.exam.vo.ExamAnalysisVo;
import com.sdut.examonline.exam.vo.ExamDetailVo;
import com.sdut.examonline.exam.vo.ExamVo;
import com.sdut.examonline.exam.vo.FinishedExamVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;

public interface ExamService extends IService<Exam> {

    /**
     * 获取用户的考试列表(如展示给学生)
     *
     * @param pagingDto 分页请求
     */
    PagingVo<ExamVo> myExams(PagingDto pagingDto);

    /**
     * 获取用户创建的考试列表
     *
     * @param pagingDto 分页请求
     */
    PagingVo<ExamVo> list(PagingDto pagingDto);

    /**
     * 获取用户下即将开始的考试
     */
    List<ExamVo> getPrestartExam();

    /**
     * 获取用户下正在进行的考试
     */
    List<ExamVo> getAnsweringExam();

    /**
     * 分页获取用户下完成的考试
     */
    PagingVo<FinishedExamVo> getFinishedExam(PagingDto pagingDto);

    /**
     * 创建考试
     *
     * @param createRequest 创建请求
     */
    ExamVo create(ExamCreateRequest createRequest);

    /**
     * 获取考试信息(仅后端内部使用)
     *
     * @param examId 考试ID
     */
    Exam getExamByIdInner(Long examId);

    /**
     * 保存考试
     *
     * @param saveRequest 保存请求
     */
    ExamVo save(ExamSaveRequest saveRequest);

    /**
     * 获取考试信息
     *
     * @param examId 考试ID
     */
    ExamVo info(Long examId);

    /**
     * 获取考试详情
     *
     * @param examId 考试ID
     */
    ExamDetailVo detail(Long examId);

    /**
     * 发布考试
     *
     * @param examId 考试ID
     */
    Boolean release(Long examId);

    /**
     * 删除考试
     *
     * @param examId 考试ID
     */
    Boolean delete(Long examId);

    /**
     * 推送即将开考消息
     *
     * @param examId 考试ID
     */
    void pushPrestart(Long examId);

    /**
     * 生成考试试卷
     *
     * @param examId 考试ID
     */
    Long generatePaper(Long examId);

    /**
     * 复制考试试卷
     *
     * @param examId 考试ID
     */
    Long duplicatePaper(Long examId);

    /**
     * 删除考试试卷
     *
     * @param examId 考试ID
     */
    Boolean deletePaper(Long examId);

    /**
     * 考试分析
     *
     * @param examId 考试ID
     */
    ExamAnalysisVo analysis(Long examId);

}
