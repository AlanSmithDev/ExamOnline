package com.sdut.examonline.question.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.question.dto.QuestionRepoCreateRequest;
import com.sdut.examonline.question.dto.QuestionRepoSaveRequest;
import com.sdut.examonline.question.dto.QuestionRepoSearchFilter;
import com.sdut.examonline.question.dto.QuestionRepoVerifyRequest;
import com.sdut.examonline.question.entity.QuestionRepo;
import com.sdut.examonline.question.vo.QuestionRepoVo;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;

public interface QuestionRepoService extends IService<QuestionRepo> {

    /**
     * 获取用户创建的所有题库
     */
    List<QuestionRepoVo> all();

    /**
     * 获取所有共享题库
     */
    List<QuestionRepoVo> allPublic();

    /**
     * 分页获取用户创建的题库
     *
     * @param pagingDto 分页请求
     */
    PagingVo<QuestionRepoVo> page(PagingDto pagingDto);

    /**
     * 分页获取共享题库
     *
     * @param pagingDto 分页请求
     */
    PagingVo<QuestionRepoVo> pagePublic(PagingDto pagingDto);

    /**
     * 分页查询题库的所有题目
     *
     * @param pagingParamDto 带参分页请求
     */
    PagingVo<QuestionVo> questions(PagingParamDto<Long> pagingParamDto);

    /**
     * 查询题库的所有题目
     *
     * @param repoId 题库ID
     */
    List<QuestionVo> questions(Long repoId);

    /**
     * 从私有题库中复制题目
     *
     * @param questionId 题目ID
     * @param destRepoId 目标题库ID
     */
    QuestionVo duplicateFromRepo(Long questionId, Long destRepoId);

    /**
     * 新建题库
     *
     * @param createRequest 创建请求
     */
    QuestionRepoVo create(QuestionRepoCreateRequest createRequest);

    /**
     * 保存题库
     *
     * @param saveRequest 保存请求
     */
    QuestionRepoVo save(QuestionRepoSaveRequest saveRequest);

    /**
     * 分页搜索题库
     *
     * @param paramDto 分页搜索请求
     */
    PagingVo<QuestionRepoVo> search(PagingParamDto<QuestionRepoSearchFilter> paramDto);

    /**
     * 是否需要验证题库密码
     *
     * @param repoId 题库ID
     */
    Boolean needVerify(Long repoId);

    /**
     * 验证题库密码
     *
     * @param request 验证请求
     */
    Boolean verify(QuestionRepoVerifyRequest request);

    /**
     * 删除题库
     *
     * @param repoId 题库ID
     */
    Boolean delete(Long repoId);

}
