package com.sdut.examonline.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.exam.dto.ExamQuestionIntelligentAddRequest;
import com.sdut.examonline.exam.dto.ExamQuestionIntelligentSaveRequest;
import com.sdut.examonline.exam.dto.ExamQuestionSetIntelligentAddRequest;
import com.sdut.examonline.exam.entity.ExamQuestionIntelligent;
import com.sdut.examonline.exam.mapper.ExamQuestionIntelligentMapper;
import com.sdut.examonline.exam.service.ExamQuestionIntelligentService;
import com.sdut.examonline.exam.vo.ExamQuestionIntelligentVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Chen
 * @since 2022/3/12 10:21
 */
@Service
public class ExamQuestionIntelligentServiceImpl extends ServiceImpl<ExamQuestionIntelligentMapper, ExamQuestionIntelligent> implements ExamQuestionIntelligentService {

    @Override
    public List<ExamQuestionIntelligentVo> allIntelligents(Long examId) {
        return MapperUtils.mapList(allIntelligentsInner(examId), ExamQuestionIntelligentVo.class);
    }

    @Override
    public List<ExamQuestionIntelligent> allIntelligentsInner(Long examId) {
        LambdaQueryWrapper<ExamQuestionIntelligent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionIntelligent::getExamId, examId);
        return super.list(wrapper);
    }

    @Override
    public PagingVo<ExamQuestionIntelligentVo> pageIntelligents(PagingParamDto<String> pagingParamDto) {
        IPage<ExamQuestionIntelligent> query = new Page<>(pagingParamDto.getPageNum(), pagingParamDto.getPageSize());
        LambdaQueryWrapper<ExamQuestionIntelligent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionIntelligent::getExamId, pagingParamDto.getParam());
        IPage<ExamQuestionIntelligent> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), ExamQuestionIntelligentVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public ExamQuestionIntelligentVo addIntelligent(ExamQuestionIntelligentAddRequest addRequest) {
        ExamQuestionIntelligent questionIntelligent = new ExamQuestionIntelligent();
        questionIntelligent.setId(SnowFlakeIdWorker.nextId());

        MapperUtils.map(addRequest, questionIntelligent);

        super.save(questionIntelligent);

        ExamQuestionIntelligentVo intelligentVo = new ExamQuestionIntelligentVo();
        MapperUtils.map(questionIntelligent, intelligentVo);

        return intelligentVo;
    }

    @Override
    public List<ExamQuestionIntelligentVo> addIntelligentSet(ExamQuestionSetIntelligentAddRequest addRequest) {
        List<ExamQuestionIntelligentVo> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(addRequest.getQuestions())) {
            for (ExamQuestionSetIntelligentAddRequest.IntelligentQuestionType question : addRequest.getQuestions()) {
                ExamQuestionIntelligentAddRequest request = new ExamQuestionIntelligentAddRequest();
                MapperUtils.map(addRequest, request);
                request.setQuestionType(question.getQuestionType());
                request.setAmount(question.getAmount());
                request.setScore(question.getScore());
                request.setDifficulty(question.getDifficulty());
                list.add(addIntelligent(request));
            }
        }
        return list;
    }

    @Override
    public ExamQuestionIntelligentVo saveIntelligent(ExamQuestionIntelligentSaveRequest saveRequest) {
        LambdaQueryWrapper<ExamQuestionIntelligent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionIntelligent::getId, saveRequest.getId());

        ExamQuestionIntelligent questionIntelligent = super.getOne(wrapper, false);

        Assert.notNull(questionIntelligent, ErrorCode.NOT_FOUND);

        MapperUtils.map(saveRequest, questionIntelligent);
        super.updateById(questionIntelligent);

        ExamQuestionIntelligentVo intelligentVo = new ExamQuestionIntelligentVo();
        MapperUtils.map(questionIntelligent, intelligentVo);

        return intelligentVo;
    }

    @Override
    public Boolean deleteIntelligent(String intelligentId) {
        LambdaQueryWrapper<ExamQuestionIntelligent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionIntelligent::getId, intelligentId);
        return super.remove(wrapper);
    }
}
