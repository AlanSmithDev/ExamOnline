package com.sdut.examonline.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Maps;
import com.sdut.examonline.common.constant.PaperRecordState;
import com.sdut.examonline.paper.constant.PaperState;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.paper.dto.PaperAnswerCorrectRequest;
import com.sdut.examonline.paper.dto.PaperAnswerSaveRequest;
import com.sdut.examonline.paper.entity.Paper;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.entity.PaperRecord;
import com.sdut.examonline.paper.mapper.PaperAnswerMapper;
import com.sdut.examonline.paper.service.PaperAnswerService;
import com.sdut.examonline.paper.service.PaperRecordService;
import com.sdut.examonline.paper.service.PaperService;
import com.sdut.examonline.paper.vo.PaperAnswerVo;
import com.sdut.examonline.paper.vo.PaperCorrectInfoVo;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PaperAnswerServiceImpl extends ServiceImpl<PaperAnswerMapper, PaperAnswer> implements PaperAnswerService {

    @Autowired
    PaperService paperService;

    @Autowired
    PaperRecordService paperRecordService;

    @Override
    public PaperAnswerVo save(PaperAnswerSaveRequest saveRequest) {
        Long userId = JwtUtils.getUserId();
        LambdaQueryWrapper<PaperAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperAnswer::getId, saveRequest.getId())
                .eq(PaperAnswer::getUserId, userId);
        PaperAnswer paperAnswer = super.getOne(wrapper, false);
        Assert.notNull(paperAnswer, ErrorCode.PAPER_QUESTION_ANSWER_NOT_FOUND);
        Assert.isTrue(userId.equals(paperAnswer.getUserId()), ErrorCode.FORBIDDEN);

        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperAnswer.getPaperId());
        Paper paper = paperService.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        Assert.isTrue(PaperState.PREPARED.equals(paper.getState()), ErrorCode.PAPER_JOIN_TIME_EXCEEDED);
        Assert.isFalse(paper.getSubmitLimit() > 0 && System.currentTimeMillis() - paper.getStartTime().getTime() < TimeUnit.MINUTES.toMillis(paper.getSubmitLimit()), ErrorCode.PAPER_SUBMIT_TIME_LIMITED);
        Assert.isFalse(System.currentTimeMillis() > paper.getEndTime().getTime(), ErrorCode.PAPER_END_TIME_EXCEEDED);

        PaperRecord paperRecord = paperRecordService.getPaperRecord(paper.getId(), JwtUtils.getUserId());
        Assert.notNull(paperRecord, ErrorCode.PAPER_QUESTION_ANSWER_NOT_FOUND);
        Assert.isFalse(PaperRecordState.SUBMITTED.equals(paperRecord.getState()), ErrorCode.PAPER_SUBMITTED);

        if (StringUtils.isNotEmpty(saveRequest.getContent())) {
            paperAnswer.setContent(saveRequest.getContent());
        }
        paperAnswer.setUpdateTime(new Date());
        super.updateById(paperAnswer);

        return MapperUtils.map(paperAnswer, PaperAnswerVo.class);
    }

    @Override
    public List<PaperAnswerVo> myAnswers(Long paperId) {
        return answers(paperId, JwtUtils.getUserId());
    }

    @Override
    public List<PaperAnswerVo> answers(Long paperId, Long userId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = paperService.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        LambdaQueryWrapper<PaperAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperAnswer::getPaperId, paperId)
                .eq(PaperAnswer::getUserId, userId);

        return MapperUtils.mapList(super.list(wrapper), PaperAnswerVo.class);
    }

    @Override
    public PaperAnswerVo correct(PaperAnswerCorrectRequest request) {
        LambdaQueryWrapper<PaperAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperAnswer::getId, request.getId());
        PaperAnswer paperAnswer = super.getOne(wrapper, false);
        Assert.notNull(paperAnswer, ErrorCode.PAPER_QUESTION_ANSWER_NOT_FOUND);

        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperAnswer.getPaperId());
        Paper paper = paperService.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        Assert.isTrue(PaperState.CORRECTING.equals(paper.getState()), ErrorCode.PAPER_NOT_CORRECTING);

        MapperUtils.map(request, paperAnswer);
        paperAnswer.setCorrector(JwtUtils.getUserId());
        paperAnswer.setUpdateTime(new Date());

        super.updateById(paperAnswer);
        return MapperUtils.map(paperAnswer, PaperAnswerVo.class);
    }

    @Override
    public PaperAnswerVo nextAnswer(Long paperId, Long questionId) {
        LambdaQueryWrapper<PaperAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperAnswer::getPaperId, paperId)
                .eq(PaperAnswer::getQuestionId, questionId)
                .eq(PaperAnswer::getCorrector, 0);
        return MapperUtils.map(super.getOne(wrapper, false), PaperAnswerVo.class);
    }

    @Override
    public PaperCorrectInfoVo getCorrectInfo(Long paperId) {
        List<QuestionDto> questions = paperService.getManualCorrectQuestions(paperId);
        List<Long> list = questions.stream().map(QuestionDto::getId).collect(Collectors.toList());
        Assert.isFalse(list.isEmpty(), ErrorCode.PAPER_NOT_CONTAINS_MANUAL_QUESTIONS);
        LambdaQueryWrapper<PaperAnswer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperAnswer::getPaperId, paperId)
                .in(PaperAnswer::getQuestionId, list);
        PaperCorrectInfoVo correctInfoVo = new PaperCorrectInfoVo();
        List<PaperAnswer> answers = super.list(wrapper);
        Map<Long, PaperCorrectInfoVo.CorrectInfo> map = Maps.newHashMapWithExpectedSize(answers.size());
        for (PaperAnswer paperAnswer : answers) {
            boolean corrected = paperAnswer.getCorrector() > 0;
            PaperCorrectInfoVo.CorrectInfo info = map.getOrDefault(paperAnswer.getQuestionId(), null);
            if (info == null) {
                info = new PaperCorrectInfoVo.CorrectInfo();
                info.setQuestionId(paperAnswer.getQuestionId());
                info.setUncorrected(0);
                info.setCorrected(0);
                map.put(paperAnswer.getQuestionId(), info);
            }
            if (corrected) {
                info.setCorrected(info.getCorrected() + 1);
            } else {
                info.setUncorrected(info.getUncorrected() + 1);
            }
        }
        correctInfoVo.setList(new ArrayList<>(map.values()));
        return correctInfoVo;
    }

}
