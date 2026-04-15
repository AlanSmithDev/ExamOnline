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
import com.sdut.examonline.exam.dto.ExamQuestionManualAddRequest;
import com.sdut.examonline.exam.dto.ExamQuestionManualSaveRequest;
import com.sdut.examonline.exam.dto.ExamQuestionSetManualAddRequest;
import com.sdut.examonline.exam.entity.ExamQuestionManual;
import com.sdut.examonline.exam.mapper.ExamQuestionManualMapper;
import com.sdut.examonline.exam.service.ExamQuestionManualService;
import com.sdut.examonline.exam.vo.ExamQuestionManualVo;
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
public class ExamQuestionManualServiceImpl extends ServiceImpl<ExamQuestionManualMapper, ExamQuestionManual> implements ExamQuestionManualService {

    @Override
    public List<ExamQuestionManualVo> allManuals(Long examId) {
        return MapperUtils.mapList(allManualsInner(examId), ExamQuestionManualVo.class);
    }

    @Override
    public List<ExamQuestionManual> allManualsInner(Long examId) {
        LambdaQueryWrapper<ExamQuestionManual> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionManual::getExamId, examId);
        return super.list(wrapper);
    }

    @Override
    public PagingVo<ExamQuestionManualVo> pageManuals(PagingParamDto<String> pagingParamDto) {
        IPage<ExamQuestionManual> query = new Page<>(pagingParamDto.getPageNum(), pagingParamDto.getPageSize());
        LambdaQueryWrapper<ExamQuestionManual> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionManual::getExamId, pagingParamDto.getParam());
        IPage<ExamQuestionManual> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), ExamQuestionManualVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public ExamQuestionManualVo addManual(ExamQuestionManualAddRequest addRequest) {
        ExamQuestionManual questionManual = new ExamQuestionManual();
        questionManual.setId(SnowFlakeIdWorker.nextId());

        MapperUtils.map(addRequest, questionManual);
        super.save(questionManual);

        ExamQuestionManualVo manualVo = new ExamQuestionManualVo();
        MapperUtils.map(questionManual, manualVo);

        return manualVo;
    }

    @Override
    public List<ExamQuestionManualVo> addManualSet(ExamQuestionSetManualAddRequest addRequest) {
        List<ExamQuestionManual> list = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(addRequest.getQuestions())) {
            for (ExamQuestionSetManualAddRequest.ManualQuestionType question : addRequest.getQuestions()) {
                ExamQuestionManual questionManual = new ExamQuestionManual();
                questionManual.setId(SnowFlakeIdWorker.nextId());
                questionManual.setExamId(addRequest.getExamId());
                questionManual.setQuestionId(question.getQuestionId());
                questionManual.setScore(question.getScore());
                list.add(questionManual);
            }
        }
        super.saveBatch(list);
        return MapperUtils.mapList(list, ExamQuestionManualVo.class);
    }

    @Override
    public ExamQuestionManualVo saveManual(ExamQuestionManualSaveRequest saveRequest) {
        LambdaQueryWrapper<ExamQuestionManual> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionManual::getId, saveRequest.getId());

        ExamQuestionManual questionManual = super.getOne(wrapper, false);

        Assert.notNull(questionManual, ErrorCode.NOT_FOUND);

        MapperUtils.map(saveRequest, questionManual);
        super.updateById(questionManual);

        ExamQuestionManualVo manualVo = new ExamQuestionManualVo();
        MapperUtils.map(questionManual, manualVo);

        return manualVo;
    }

    @Override
    public Boolean deleteManual(String manualId) {
        LambdaQueryWrapper<ExamQuestionManual> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ExamQuestionManual::getId, manualId);
        return super.remove(wrapper);
    }
}
