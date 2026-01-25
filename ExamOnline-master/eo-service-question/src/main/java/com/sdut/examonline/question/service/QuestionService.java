package com.sdut.examonline.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.dubbo.dto.QuestionIntelligentDto;
import com.sdut.examonline.question.dto.QuestionCreateRequest;
import com.sdut.examonline.question.dto.QuestionDetailCreateRequest;
import com.sdut.examonline.question.dto.QuestionSaveRequest;
import com.sdut.examonline.question.dto.QuestionSearchFilter;
import com.sdut.examonline.question.entity.Question;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.io.InputStream;
import java.util.List;

public interface QuestionService extends IService<Question> {

    void fillTestData(Long repoId);

    void fillTestData(Long repoId, Long userId);

    /**
     * 获取题目详细信息
     *
     * @param questionId 题目ID
     */
    QuestionVo detail(Long questionId);

    /**
     * 新增题目
     *
     * @param createRequest 创建请求
     */
    QuestionVo add(QuestionCreateRequest createRequest);

    /**
     * 新增题目，与预选答案一起提交
     *
     * @param createRequest 创建请求
     */
    QuestionVo addDetail(QuestionDetailCreateRequest createRequest);

    /**
     * 保存题目
     *
     * @param saveRequest 保存请求
     */
    QuestionVo save(QuestionSaveRequest saveRequest);

    /**
     * 分页搜索题目
     *
     * @param paramDto 分页搜索请求
     */
    PagingVo<QuestionVo> search(PagingParamDto<QuestionSearchFilter> paramDto);

    /**
     * 查询题目(内部调用)
     *
     * @param questionId 题目ID
     */
    Question getQuestionByIdInner(Long questionId);

    /**
     * 删除题目
     *
     * @param questionId 题目ID
     */
    Boolean delete(Long questionId);

    /**
     * 随机抽取试题(内部调用)
     *
     * @param setting 抽取设定
     */
    List<QuestionDto> randomGet(QuestionIntelligentDto setting);

    /**
     * 从Excel导入试题
     */
    List<QuestionVo> importFromExcel(Long repoId, InputStream inputStream);

    /**
     * 删除题目缓存
     *
     * @param questionId 题目ID
     */
    void removeQuestionCache(Long questionId);

}
