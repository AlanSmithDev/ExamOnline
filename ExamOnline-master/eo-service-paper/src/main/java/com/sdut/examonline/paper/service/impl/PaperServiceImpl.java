package com.sdut.examonline.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sdut.examonline.common.constant.PaperRecordState;
import com.sdut.examonline.paper.constant.PaperState;
import com.sdut.examonline.common.constant.QuestionType;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.dubbo.client.MessageDubboService;
import com.sdut.examonline.dubbo.client.QuestionDubboService;
import com.sdut.examonline.dubbo.dto.*;
import com.sdut.examonline.paper.corrector.Corrector;
import com.sdut.examonline.paper.entity.Paper;
import com.sdut.examonline.paper.entity.PaperAnswer;
import com.sdut.examonline.paper.entity.PaperQuestion;
import com.sdut.examonline.paper.entity.PaperRecord;
import com.sdut.examonline.paper.mapper.PaperMapper;
import com.sdut.examonline.paper.redis.RedisKeyConstants;
import com.sdut.examonline.paper.service.PaperAnswerService;
import com.sdut.examonline.paper.service.PaperQuestionService;
import com.sdut.examonline.paper.service.PaperRecordService;
import com.sdut.examonline.paper.service.PaperService;
import com.sdut.examonline.paper.vo.*;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class PaperServiceImpl extends ServiceImpl<PaperMapper, Paper> implements PaperService {

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    PaperAnswerService paperAnswerService;

    @Autowired
    PaperQuestionService paperQuestionService;

    @Autowired
    PaperRecordService paperRecordService;

    @Reference(check = false)
    QuestionDubboService questionDubboService;

    @Reference(check = false)
    MessageDubboService messageDubboService;

    @Autowired
    Corrector corrector;

    @Autowired
    CacheUtils cacheUtils;

    @Override
    public Long createManual(PaperSettingDto paperSetting, List<QuestionManualDto> settings) {
        if (CollectionUtils.isEmpty(settings)) {
            return 0L;
        }
        Paper paper = new Paper();
        paper.setId(SnowFlakeIdWorker.nextId());
        paper.setTitle(paperSetting.getTitle());
        paper.setTimeLimit(paperSetting.getTimeLimit());
        paper.setFaceCheck(paperSetting.getFaceCheck());
        paper.setJoinLimit(paperSetting.getJoinLimit());
        paper.setSubmitLimit(paperSetting.getSubmitLimit());
        paper.setState(PaperState.PREPARED);
        paper.setStartTime(new Date(paperSetting.getStartTime()));
        Date now = new Date();
        paper.setCreateTime(now);
        paper.setUpdateTime(now);

        super.save(paper);

        taskExecutor.execute(() -> {
            List<PaperQuestion> questions = Lists.newArrayList();
            int score = 0;
            for (QuestionManualDto setting : settings) {
                if (setting.getQuestionId() != null && setting.getQuestionId() > 0) {
                    QuestionDto question = questionDubboService.getQuestionById(setting.getQuestionId());

                    Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);

                    PaperQuestion paperQuestion = new PaperQuestion();
                    paperQuestion.setId(SnowFlakeIdWorker.nextId());
                    paperQuestion.setPaperId(paper.getId());
                    paperQuestion.setQuestionId(setting.getQuestionId());
                    paperQuestion.setScore(setting.getScore());
                    if (CollectionUtils.isEmpty(question.getSubQuestions())) {
                        score += paperQuestion.getScore();
                    } else {
                        score += paperQuestion.getScore() * question.getSubQuestions().size();
                    }
                    questions.add(paperQuestion);
                }
            }
            paperQuestionService.saveBatch(questions);
            paper.setTotalScore(score);
            super.updateById(paper);

            insertPaperRecordBatch(paper.getId(), paperSetting.getParticipants());
        });


        return paper.getId();
    }

    @Override
    public Long createIntelligent(PaperSettingDto paperSetting, List<QuestionIntelligentDto> settings) {
        if (CollectionUtils.isEmpty(settings)) {
            return 0L;
        }
        Paper paper = new Paper();
        paper.setId(SnowFlakeIdWorker.nextId());
        paper.setTitle(paperSetting.getTitle());
        paper.setTimeLimit(paperSetting.getTimeLimit());
        paper.setFaceCheck(paperSetting.getFaceCheck());
        paper.setJoinLimit(paperSetting.getJoinLimit());
        paper.setSubmitLimit(paperSetting.getSubmitLimit());
        paper.setState(PaperState.PREPARED);
        paper.setStartTime(new Date(paperSetting.getStartTime()));
        paper.setEndTime(new Date(paperSetting.getEndTime()));
        Date now = new Date();
        paper.setCreateTime(now);
        paper.setUpdateTime(now);

        super.save(paper);

        taskExecutor.execute(() -> {
            List<PaperQuestion> questions = Lists.newArrayList();
            int score = 0;
            for (QuestionIntelligentDto setting : settings) {
                if (setting.getRepoId() != null && setting.getRepoId() > 0) {

                    List<QuestionDto> list = questionDubboService.randomGet(setting);

                    for (QuestionDto question : list) {
                        PaperQuestion paperQuestion = new PaperQuestion();
                        paperQuestion.setId(SnowFlakeIdWorker.nextId());
                        paperQuestion.setPaperId(paper.getId());
                        paperQuestion.setQuestionId(question.getId());
                        paperQuestion.setScore(setting.getScore());
                        questions.add(paperQuestion);
                        if (CollectionUtils.isEmpty(question.getSubQuestions())) {
                            score += paperQuestion.getScore();
                        } else {
                            score += paperQuestion.getScore() * question.getSubQuestions().size();
                        }
                    }
                }
            }
            paperQuestionService.saveBatch(questions);
            paper.setTotalScore(score);
            super.updateById(paper);
            insertPaperRecordBatch(paper.getId(), paperSetting.getParticipants());
        });

        return paper.getId();
    }

    private void insertPaperRecordBatch(Long paperId, List<Long> userIds) {
        Date now = new Date();

        LambdaQueryWrapper<PaperQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperQuestion::getPaperId, paperId);
        List<PaperQuestion> questions = paperQuestionService.list(queryWrapper);

        List<PaperRecord> records = Lists.newArrayListWithExpectedSize(userIds.size());
        List<PaperAnswer> paperAnswers = Lists.newArrayList();
        Map<Long, QuestionDto> questionDtoMap = Maps.newHashMap();
        for (PaperQuestion question : questions) {
            questionDtoMap.put(question.getQuestionId(), questionDubboService.getQuestionById(question.getQuestionId()));
        }
        PaperRecord paperRecord;
        for (Long userId : userIds) {
            paperRecord = new PaperRecord();
            paperRecord.setId(SnowFlakeIdWorker.nextId());
            paperRecord.setPaperId(paperId);
            paperRecord.setUserId(userId);
            paperRecord.setScore(0);
            paperRecord.setState(PaperRecordState.PREPARED);
            paperRecord.setStartTime(now);
            paperRecord.setCreateTime(now);
            paperRecord.setUpdateTime(now);
            records.add(paperRecord);

            for (PaperQuestion paperQuestion : questions) {
                QuestionDto questionDto = questionDtoMap.get(paperQuestion.getQuestionId());

                if (CollectionUtils.isNotEmpty(questionDto.getSubQuestions())) {
                    for (QuestionDto subQuestion : questionDto.getSubQuestions()) {
                        PaperAnswer paperAnswer = new PaperAnswer();
                        paperAnswer.setId(SnowFlakeIdWorker.nextId());
                        paperAnswer.setPaperId(paperQuestion.getPaperId());
                        paperAnswer.setQuestionId(subQuestion.getId());
                        paperAnswer.setUserId(userId);
                        paperAnswer.setContent(null);
                        paperAnswer.setIsRight(false);
                        paperAnswer.setScore(0);
                        paperAnswer.setCorrector(0L);
                        paperAnswer.setCreateTime(now);
                        paperAnswer.setUpdateTime(now);

                        paperAnswers.add(paperAnswer);
                    }
                } else {
                    PaperAnswer paperAnswer = new PaperAnswer();
                    paperAnswer.setId(SnowFlakeIdWorker.nextId());
                    paperAnswer.setPaperId(paperQuestion.getPaperId());
                    paperAnswer.setQuestionId(paperQuestion.getQuestionId());
                    paperAnswer.setUserId(userId);
                    paperAnswer.setContent(null);
                    paperAnswer.setIsRight(false);
                    paperAnswer.setScore(0);
                    paperAnswer.setCorrector(0L);
                    paperAnswer.setCreateTime(now);
                    paperAnswer.setUpdateTime(now);

                    paperAnswers.add(paperAnswer);
                }
            }
        }
        paperRecordService.saveBatch(records);
        paperAnswerService.saveBatch(paperAnswers);
    }

    @Override
    public PaperVo get(Long paperId) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(wrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);
        return MapperUtils.map(paper, PaperVo.class);
    }

    @Override
    public PaperDetailVo detail(Long paperId) {
        LambdaQueryWrapper<Paper> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Paper::getId, paperId);

        Paper paper = super.getOne(wrapper, false);

        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        PaperDetailVo detailVo = new PaperDetailVo();
        MapperUtils.map(paper, detailVo);

        LambdaQueryWrapper<PaperQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperQuestion::getPaperId, paperId);

        List<PaperQuestionDetailVo> paperQuestions = Lists.newArrayList();
        for (PaperQuestion paperQuestion : paperQuestionService.list(queryWrapper)) {
            PaperQuestionDetailVo questionVo = new PaperQuestionDetailVo();
            MapperUtils.map(paperQuestion, questionVo);

            QuestionDto question = questionDubboService.getQuestionById(paperQuestion.getQuestionId());

            Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);

            questionVo.setType(question.getType());
            questionVo.setContent(question.getContent());
            questionVo.setVoiceFile(question.getVoiceFile());

            List<QuestionAnswerDto> answers = questionDubboService.getAnswersByQuestionId(question.getId());
            questionVo.setAnswers(MapperUtils.mapList(answers, PaperQuestionAnswerVo.class));
            paperQuestions.add(questionVo);

            if (CollectionUtils.isEmpty(question.getSubQuestions())) {
                questionVo.setSubQuestions(Collections.emptyList());
            } else {
                int score = 0;
                List<PaperQuestionDetailVo> subQuestions = Lists.newArrayList();
                for (QuestionDto subQuestion : question.getSubQuestions()) {
                    PaperQuestionDetailVo subQuestionVo = new PaperQuestionDetailVo();
                    subQuestionVo.setPaperId(paperQuestion.getPaperId());
                    subQuestionVo.setQuestionId(subQuestion.getId());
                    subQuestionVo.setScore(paperQuestion.getScore());
                    subQuestionVo.setType(subQuestion.getType());
                    subQuestionVo.setContent(subQuestion.getContent());
                    subQuestion.setVoiceFile(subQuestion.getVoiceFile());
                    subQuestionVo.setAnswers(MapperUtils.mapList(questionDubboService.getAnswersByQuestionId(subQuestion.getId()), PaperQuestionAnswerVo.class));
                    subQuestionVo.setSubQuestions(Collections.emptyList());
                    subQuestions.add(subQuestionVo);
                    score += paperQuestion.getScore();
                }
                questionVo.setScore(score);
                questionVo.setSubQuestions(subQuestions);
            }
        }

        detailVo.setQuestions(paperQuestions);
        return detailVo;
    }

    @Override
    public Long duplicate(Long paperId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);

        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        Date now = new Date();

        paper.setId(SnowFlakeIdWorker.nextId());
        paper.setUpdateTime(now);
        paper.setCreateTime(now);
        super.save(paper);

        LambdaQueryWrapper<PaperQuestion> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(PaperQuestion::getPaperId, paperId);
        List<PaperQuestion> questions = paperQuestionService.list(questionWrapper);
        for (PaperQuestion paperQuestion : questions) {
            paperQuestion.setId(SnowFlakeIdWorker.nextId());
            paperQuestion.setPaperId(paper.getId());
        }
        paperQuestionService.saveBatch(questions);

        return paper.getId();
    }

    @Override
    public Boolean delete(Long paperId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        boolean paperRemoved = super.remove(paperWrapper);

        LambdaQueryWrapper<PaperQuestion> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(PaperQuestion::getPaperId, paperId);
        boolean questionRemoved = paperQuestionService.remove(questionWrapper);

        LambdaQueryWrapper<PaperAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper.eq(PaperAnswer::getPaperId, paperId);
        boolean answerRemoved = paperAnswerService.remove(answerWrapper);

        LambdaQueryWrapper<PaperRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper.eq(PaperRecord::getPaperId, paperId);
        boolean recordRemoved = paperRecordService.remove(recordWrapper);

        return paperRemoved || questionRemoved || answerRemoved || recordRemoved;
    }

    @Override
    public PaperRecordVo startAnswer(Long paperId, Long userId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);

        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);
        Assert.isTrue(PaperState.PREPARED.equals(paper.getState()), ErrorCode.PAPER_NOT_ANSWERING);
        Assert.isFalse(paper.getJoinLimit() > 0 && System.currentTimeMillis() - paper.getStartTime().getTime() > TimeUnit.MINUTES.toMillis(paper.getJoinLimit()), ErrorCode.PAPER_JOIN_TIME_EXCEEDED);

        cacheUtils.setExpireAt(RedisKeyConstants.PAPER_END_TIME + paper.getId(), paper.getId(), paper.getEndTime());

        if (paper.getFaceCheck() && needCheckFace(paperId, userId)) {
            throw new ServiceException(ErrorCode.PAPER_NEED_CHECK_FACE);
        }

        PaperRecord paperRecord = paperRecordService.getPaperRecord(paperId, userId);

        if (paperRecord != null) {
            Assert.isFalse(PaperRecordState.SUBMITTED.equals(paperRecord.getState()), ErrorCode.PAPER_SUBMITTED);
            return MapperUtils.map(paperRecord, PaperRecordVo.class);
        }

        Date now = new Date();
        paperRecord = new PaperRecord();
        paperRecord.setId(SnowFlakeIdWorker.nextId());
        paperRecord.setPaperId(paperId);
        paperRecord.setUserId(userId);
        paperRecord.setScore(0);
        paperRecord.setState(PaperRecordState.PREPARED);
        paperRecord.setStartTime(now);
        paperRecord.setCreateTime(now);
        paperRecord.setUpdateTime(now);
        paperRecordService.save(paperRecord);

        List<PaperAnswer> paperAnswers = Lists.newArrayList();
        LambdaQueryWrapper<PaperQuestion> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PaperQuestion::getPaperId, paperId);
        for (PaperQuestion paperQuestion : paperQuestionService.list(queryWrapper)) {

            QuestionDto questionDto = questionDubboService.getQuestionById(paperQuestion.getQuestionId());

            if (CollectionUtils.isNotEmpty(questionDto.getSubQuestions())) {
                for (QuestionDto subQuestion : questionDto.getSubQuestions()) {
                    PaperAnswer paperAnswer = new PaperAnswer();
                    paperAnswer.setId(SnowFlakeIdWorker.nextId());
                    paperAnswer.setPaperId(paperQuestion.getPaperId());
                    paperAnswer.setQuestionId(subQuestion.getId());
                    paperAnswer.setUserId(userId);
                    paperAnswer.setContent(null);
                    paperAnswer.setIsRight(false);
                    paperAnswer.setScore(0);
                    paperAnswer.setCorrector(0L);
                    paperAnswer.setCreateTime(now);
                    paperAnswer.setUpdateTime(now);

                    paperAnswers.add(paperAnswer);
                }
            } else {
                PaperAnswer paperAnswer = new PaperAnswer();
                paperAnswer.setId(SnowFlakeIdWorker.nextId());
                paperAnswer.setPaperId(paperQuestion.getPaperId());
                paperAnswer.setQuestionId(paperQuestion.getQuestionId());
                paperAnswer.setUserId(userId);
                paperAnswer.setContent(null);
                paperAnswer.setIsRight(false);
                paperAnswer.setScore(0);
                paperAnswer.setCorrector(0L);
                paperAnswer.setCreateTime(now);
                paperAnswer.setUpdateTime(now);

                paperAnswers.add(paperAnswer);
            }
        }
        paperAnswerService.saveBatch(paperAnswers);

        return MapperUtils.map(paperRecord, PaperRecordVo.class);
    }

    @Override
    public Boolean needCheckFace(Long paperId, Long userId) {
        String value = cacheUtils.getString("paper:cf:" + paperId + "_" + userId);
        return cacheUtils.isNullString(value) || StringUtils.isEmpty(value) || !"checked".equals(value);
    }

    @Override
    public PaperRecordVo submit(Long paperId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);

        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);
        Assert.isFalse(paper.getSubmitLimit() > 0 && System.currentTimeMillis() - paper.getStartTime().getTime() <= TimeUnit.MINUTES.toMillis(paper.getSubmitLimit()), ErrorCode.PAPER_SUBMIT_TIME_LIMITED);
        Assert.isFalse(System.currentTimeMillis() > paper.getEndTime().getTime(), ErrorCode.PAPER_END_TIME_EXCEEDED);

        Long userId = JwtUtils.getUserId();
        PaperRecord paperRecord = paperRecordService.getPaperRecord(paperId, userId);
        Assert.notNull(paperRecord, ErrorCode.PAPER_QUESTION_ANSWER_NOT_FOUND);
        Assert.isFalse(PaperRecordState.SUBMITTED.equals(paperRecord.getState()), ErrorCode.PAPER_SUBMITTED);

        paperRecord.setState(PaperRecordState.SUBMITTED);
        paperRecord.setEndTime(new Date());
        paperRecordService.updateById(paperRecord);
        long delt = paper.getEndTime().getTime() - paperRecord.getEndTime().getTime();
        if (delt >= 3L * 60L * 1000L) {
            taskExecutor.execute(() -> {
                try {
                    correct(paper.getId(), userId);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }

        return MapperUtils.map(paperRecord, PaperRecordVo.class);
    }

    @Override
    public void correct(Long paperId, Long userId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);
        paper.setState(PaperState.CORRECTING);
        super.updateById(paper);

        LambdaQueryWrapper<PaperQuestion> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(PaperQuestion::getPaperId, paperId);
        List<PaperQuestion> paperQuestions = paperQuestionService.list(questionWrapper);

        LambdaQueryWrapper<PaperAnswer> answerWrapper = new LambdaQueryWrapper<>();
        answerWrapper
                .eq(PaperAnswer::getPaperId, paperId)
                .eq(PaperAnswer::getUserId, userId);
        List<PaperAnswer> paperAnswers = paperAnswerService.list(answerWrapper);

        Date now = new Date();

        /*
         * 已批改题数
         * 用于判断是否批改完毕
         * 否则需要人工批改
         * */
        for (PaperQuestion paperQuestion : paperQuestions) {
            QuestionDto question = questionDubboService.getQuestionById(paperQuestion.getQuestionId());
            if (CollectionUtils.isNotEmpty(question.getSubQuestions())) {
                for (QuestionDto subQuestion : question.getSubQuestions()) {
                    PaperQuestion paperSubQuestion = new PaperQuestion();
                    MapperUtils.map(paperQuestion, paperSubQuestion);
                    paperSubQuestion.setQuestionId(subQuestion.getId());
                    correctQuestion(paperSubQuestion, question, paperAnswers, now);
                }
                continue;
            }
            correctQuestion(paperQuestion, question, paperAnswers, now);
        }

        paperAnswerService.updateBatchById(paperAnswers);

        int total = 0;
        for (PaperAnswer paperAnswer : paperAnswers) {
            total += paperAnswer.getScore();
        }

        PaperRecord paperRecord = paperRecordService.getPaperRecord(paperId, userId);
        paperRecord.setScore(total);
        paperRecord.setUpdateTime(now);
        paperRecord.setState(PaperRecordState.CORRECTED);
        if (paperRecord.getEndTime() == null) {
            paperRecord.setEndTime(new Date());
        }
        paperRecordService.updateById(paperRecord);
    }

    @Override
    public void correctPaper(Long paperId) {
        if (!cacheUtils.setIfAbsent(RedisKeyConstants.PAPER_CORRECTING + paperId, true, 3, TimeUnit.MINUTES)) {
            return;
        }
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);
        paper.setState(PaperState.CORRECTING);
        super.updateById(paper);

        LambdaQueryWrapper<PaperQuestion> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(PaperQuestion::getPaperId, paperId);
        List<PaperQuestion> paperQuestions = paperQuestionService.list(questionWrapper);

        LambdaQueryWrapper<PaperRecord> recordWrapper = new LambdaQueryWrapper<>();
        recordWrapper
                .eq(PaperRecord::getPaperId, paperId);
        List<PaperRecord> records = paperRecordService.list(recordWrapper);
        Map<Long, QuestionDto> map = Maps.newHashMapWithExpectedSize(paperQuestions.size());
        for (PaperQuestion paperQuestion : paperQuestions) {
            QuestionDto question = questionDubboService.getQuestionById(paperQuestion.getQuestionId());
            map.put(paperQuestion.getQuestionId(), question);
        }
        Iterator<PaperRecord> iterator = records.iterator();
        Date now = new Date();

        AtomicBoolean allCorrected = new AtomicBoolean(true);
        CountDownLatch countDownLatch = new CountDownLatch(records.size());
        taskExecutor.execute(() -> {
            while (iterator.hasNext()) {
                try {
                    PaperRecord paperRecord = iterator.next();
                    if (PaperRecordState.PREPARED.equals(paperRecord.getState()) || PaperRecordState.CORRECTED.equals(paperRecord.getState())) {
                        continue;
                    }

                    LambdaQueryWrapper<PaperAnswer> answerWrapper = new LambdaQueryWrapper<>();
                    answerWrapper
                            .eq(PaperAnswer::getPaperId, paperId)
                            .eq(PaperAnswer::getUserId, paperRecord.getUserId());
                    List<PaperAnswer> paperAnswers = paperAnswerService.list(answerWrapper);

                    for (PaperQuestion paperQuestion : paperQuestions) {
                        QuestionDto question = map.get(paperQuestion.getQuestionId());
                        if (question == null) {
                            continue;
                        }
                        if (CollectionUtils.isNotEmpty(question.getSubQuestions())) {
                            for (QuestionDto subQuestion : question.getSubQuestions()) {
                                PaperQuestion paperSubQuestion = new PaperQuestion();
                                MapperUtils.map(paperQuestion, paperSubQuestion);
                                paperSubQuestion.setQuestionId(subQuestion.getId());
                                if (!correctQuestion(paperSubQuestion, question, paperAnswers, now)) {
                                    allCorrected.set(false);
                                }
                            }
                            continue;
                        }
                        if (!correctQuestion(paperQuestion, question, paperAnswers, now)) {
                            allCorrected.set(false);
                        }
                    }
                    int total = 0;
                    for (PaperAnswer paperAnswer : paperAnswers) {
                        total += paperAnswer.getScore();
                    }
                    paperRecord.setScore(total);
                    paperRecord.setState(PaperRecordState.CORRECTED);
                    paperRecord.setUpdateTime(now);
                    if (paperRecord.getEndTime() == null) {
                        paperRecord.setEndTime(now);
                    }
                    if (allCorrected.get()) {
                        paperRecord.setState(PaperState.CORRECT_COMPLETED);
                    }
                    paperAnswerService.updateBatchById(paperAnswers);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                } finally {
                    countDownLatch.countDown();
                }
            }
        });

        try {
            countDownLatch.await();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }

        if (allCorrected.get()) {
            paper.setState(PaperState.CORRECT_COMPLETED);
            super.updateById(paper);
            taskExecutor.execute(() -> {
                MessageDto messageDto = new MessageDto();
                messageDto.setTitle("试卷批改完毕");
                messageDto.setContent("你最新的成绩分析已生成！");
                messageDubboService.createAndSendMessage(messageDto, records.stream().map(PaperRecord::getUserId).collect(Collectors.toList()));
            });
        }
        paperRecordService.updateBatchById(records);
        cacheUtils.delete(RedisKeyConstants.PAPER_CORRECTING + paperId);
    }

    @Override
    public List<QuestionDto> getManualCorrectQuestions(Long paperId) {
        List<QuestionDto> questions = Lists.newArrayList();
        LambdaQueryWrapper<PaperQuestion> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(PaperQuestion::getPaperId, paperId);
        List<PaperQuestion> paperQuestions = paperQuestionService.list(questionWrapper);

        for (PaperQuestion paperQuestion : paperQuestions) {
            QuestionDto question = questionDubboService.getQuestionById(paperQuestion.getQuestionId());
            if (question == null) {
                continue;
            }
            if (CollectionUtils.isNotEmpty(question.getSubQuestions())) {
                for (QuestionDto subQuestion : question.getSubQuestions()) {
                    if (QuestionType.isManualCorrect(subQuestion.getType())) {
                        questions.add(subQuestion);
                    }
                }
                continue;
            }
            if (QuestionType.isManualCorrect(question.getType())) {
                questions.add(question);
            }
        }
        return questions;
    }

    @Override
    public Map<String, Object> exportPaper(Long paperId) {
        LambdaQueryWrapper<Paper> paperWrapper = new LambdaQueryWrapper<>();
        paperWrapper.eq(Paper::getId, paperId);
        Paper paper = super.getOne(paperWrapper, false);
        Assert.notNull(paper, ErrorCode.PAPER_NOT_FOUND);

        PaperDetailVo detail = detail(paperId);
        List<PoiQuestionSetVo> questionSets = Lists.newArrayList();
        String[] numStr = new String[]{"一", "二", "三", "四", "五", "六", "七", "八", "九"};
        int quetionSetNum = 0;
        for (int i = 0; i <= QuestionType.SHORT_ANSWER; i++) {
            final Integer type = i;
            List<PaperQuestionDetailVo> questionDetailVos = detail.getQuestions().stream().filter(question -> type.equals(question.getType())).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(questionDetailVos)) {
                continue;
            }
            PoiQuestionSetVo questionSetVo = new PoiQuestionSetVo();
            questionSetVo.setTitle(numStr[quetionSetNum++] + "、" + QuestionType.getName(type));
            List<PoiQuestionVo> questionVos = Lists.newArrayList();
            questionSetVo.setQuestions(questionVos);
            for (PaperQuestionDetailVo questionDetailVo : questionDetailVos) {
                PoiQuestionVo poiQuestionVo = wrapPoiQuestion(type, questionDetailVo, questionVos.size() + 1, false);
                if (CollectionUtils.isNotEmpty(questionDetailVo.getSubQuestions())) {
                    List<PoiQuestionVo> subQuestions = Lists.newArrayList();
                    for (PaperQuestionDetailVo subQuestion : questionDetailVo.getSubQuestions()) {
                        subQuestions.add(wrapPoiQuestion(subQuestion.getType(), subQuestion, subQuestions.size() + 1, true));
                    }
                    poiQuestionVo.setSubQuestions(subQuestions);
                } else {
                    poiQuestionVo.setSubQuestions(Collections.emptyList());
                }
                questionVos.add(poiQuestionVo);
            }
            questionSets.add(questionSetVo);
        }

        Map<String, Object> options = Maps.newHashMapWithExpectedSize(2);
        options.put("exam", paper.getTitle());
        options.put("total", paper.getTotalScore());
        StringBuilder sb = new StringBuilder();
        int diff = (int) ((paper.getEndTime().getTime() - paper.getStartTime().getTime()) / 1000);
        int hour = diff / (60 * 60);
        int min = diff % (60 * 60) / 60;
        int second = diff % (60 * 60) % 60;
        if (hour > 0) {
            sb.append(hour).append("小时");
        }
        if (min > 0) {
            sb.append(min).append("分钟");
        }
        if (second > 0) {
            sb.append(second).append("秒");
        }
        options.put("duration", sb.toString());
        options.put("questionSets", questionSets);
        return options;
    }

    private PoiQuestionVo wrapPoiQuestion(Integer type, PaperQuestionDetailVo questionDetailVo, int num, boolean subQuestion) {
        PoiQuestionVo poiQuestionVo = new PoiQuestionVo();
        poiQuestionVo.setNum(subQuestion ? "(" + num + ")" : "" + num);
        poiQuestionVo.setScore(questionDetailVo.getScore());
        poiQuestionVo.setContent(questionDetailVo.getContent());
        if (QuestionType.JUDGEMENT.equals(type)) {
            poiQuestionVo.setContent(poiQuestionVo.getContent() + "     (  )");
            poiQuestionVo.setAnswers("");
        } else if (QuestionType.COMPLETION.equals(type)) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < questionDetailVo.getAnswers().size(); i++) {
                sb.append("____________________\n");
            }
            poiQuestionVo.setAnswers(sb.toString());
        } else if (QuestionType.NOUN_INTERPRETATION.equals(type) || QuestionType.SHORT_ANSWER.equals(type)) {
            poiQuestionVo.setAnswers("_______________________________________________________________________________________________________________________________________________________________________________________________________________");
        } else {
            StringBuilder sb = new StringBuilder();
            int optionNum = 0;
            for (PaperQuestionAnswerVo answer : questionDetailVo.getAnswers()) {
                sb.append((char) (65 + (optionNum++))).append(". ").append(answer.getContent()).append("\n");
            }
            poiQuestionVo.setAnswers(sb.toString());
        }
        return poiQuestionVo;
    }

    private boolean correctQuestion(PaperQuestion paperQuestion, QuestionDto question, List<PaperAnswer> paperAnswers, Date time) {
        PaperAnswer paperAnswer = null;
        for (PaperAnswer ele : paperAnswers) {
            if (paperQuestion.getQuestionId().equals(ele.getQuestionId())) {
                paperAnswer = ele;
                break;
            }
        }
        List<QuestionAnswerDto> answers = questionDubboService.getAnswersByQuestionId(paperQuestion.getQuestionId());
        if (!QuestionType.JUDGEMENT.equals(question.getType())) {
            answers.removeIf(answer -> !answer.getIsRight());
        }
        boolean result = corrector.correct(paperQuestion, paperAnswer, question, answers);
        if (result) {
            if (paperAnswer != null) {
                paperAnswer.setUpdateTime(time);
            }
        }
        return result;
    }

}
