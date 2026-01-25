package com.sdut.examonline.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.exam.dto.ExamQuestionManualAddRequest;
import com.sdut.examonline.exam.dto.ExamQuestionManualSaveRequest;
import com.sdut.examonline.exam.dto.ExamQuestionSetManualAddRequest;
import com.sdut.examonline.exam.entity.ExamQuestionManual;
import com.sdut.examonline.exam.vo.ExamQuestionManualVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;

public interface ExamQuestionManualService extends IService<ExamQuestionManual> {

    /**
     * 查询试卷所有手动组卷试题
     *
     * @param examId 考试ID
     */
    List<ExamQuestionManualVo> allManuals(Long examId);

    /**
     * 查询试卷所有手动组卷试题(内部调用)
     *
     * @param examId 考试ID
     */
    List<ExamQuestionManual> allManualsInner(Long examId);

    /**
     * 分页查询试卷所有手动组卷试题
     *
     * @param pagingParamDto 带参分页请求
     */
    PagingVo<ExamQuestionManualVo> pageManuals(PagingParamDto<String> pagingParamDto);

    /**
     * 新增手动组卷试题列表
     *
     * @param addRequest 新增请求
     */
    ExamQuestionManualVo addManual(ExamQuestionManualAddRequest addRequest);

    /**
     * 新增手动组卷试题
     *
     * @param addRequest 新增请求
     */
    List<ExamQuestionManualVo> addManualSet(ExamQuestionSetManualAddRequest addRequest);

    /**
     * 保存手动组卷试题
     *
     * @param saveRequest 保存请求
     */
    ExamQuestionManualVo saveManual(ExamQuestionManualSaveRequest saveRequest);

    /**
     * 删除手动组卷试题
     *
     * @param manualId 试卷ID
     */
    Boolean deleteManual(String manualId);

}
