package com.sdut.examonline.exam.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sdut.examonline.common.constant.ExamState;
import com.sdut.examonline.common.constant.PaperJoinType;
import com.sdut.examonline.common.constant.PaperRecordState;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.dubbo.client.DepartDubboService;
import com.sdut.examonline.dubbo.client.PaperDubboService;
import com.sdut.examonline.dubbo.dto.*;
import com.sdut.examonline.exam.dto.ExamCreateRequest;
import com.sdut.examonline.exam.dto.ExamSaveRequest;
import com.sdut.examonline.exam.entity.Exam;
import com.sdut.examonline.exam.entity.ExamQuestionIntelligent;
import com.sdut.examonline.exam.entity.ExamQuestionManual;
import com.sdut.examonline.exam.mapper.ExamMapper;
import com.sdut.examonline.exam.redis.RedisKeyConstants;
import com.sdut.examonline.exam.service.ExamQuestionIntelligentService;
import com.sdut.examonline.exam.service.ExamQuestionManualService;
import com.sdut.examonline.exam.service.ExamService;
import com.sdut.examonline.exam.vo.*;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl extends ServiceImpl<ExamMapper, Exam> implements ExamService {

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    ExamQuestionManualService manualService;

    @Autowired
    ExamQuestionIntelligentService intelligentService;

    @Reference(check = false)
    PaperDubboService paperDubboService;

    @Reference(check = false)
    DepartDubboService departDubboService;

    @Autowired
    CacheUtils cacheUtils;

    @Override
    public PagingVo<ExamVo> myExams(PagingDto pagingDto) {
        IPage<Exam> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();

        List<Long> idList = departDubboService.getUserDepartId(JwtUtils.getUserId());

        wrapper.isNull(Exam::getDepartIds);
        if (!idList.isEmpty()) {
            for (Long id : idList) {
                wrapper.or().like(Exam::getDepartIds, id);
            }
        }
        wrapper.eq(Exam::getState, ExamState.RELEASED);
        IPage<Exam> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), ExamVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public PagingVo<ExamVo> list(PagingDto pagingDto) {
        IPage<Exam> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getCreator, JwtUtils.getUserId());
        IPage<Exam> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), ExamVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public List<ExamVo> getPrestartExam() {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        List<Long> idList = departDubboService.getUserDepartId(JwtUtils.getUserId());

        wrapper.eq(Exam::getState, ExamState.RELEASED)
                .ge(Exam::getStartTime, new Date());

        wrapper.and(w -> {
            w.isNull(Exam::getDepartIds);
            if (!idList.isEmpty()) {
                for (Long id : idList) {
                    w.or().like(Exam::getDepartIds, id);
                }
            }
        });
        List<Exam> list = super.list(wrapper);
        list.sort((o1, o2) -> (int) (o1.getStartTime().getTime() - o2.getStartTime().getTime()));
        return MapperUtils.mapList(list, ExamVo.class);
    }

    @Override
    public List<ExamVo> getAnsweringExam() {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        List<Long> idList = departDubboService.getUserDepartId(JwtUtils.getUserId());

        Date now = new Date();
        wrapper.eq(Exam::getState, ExamState.RELEASED)
                .le(Exam::getStartTime, now)
                .gt(Exam::getEndTime, now);

        wrapper.and(w -> {
            w.isNull(Exam::getDepartIds);
            if (!idList.isEmpty()) {
                for (Long id : idList) {
                    w.or().like(Exam::getDepartIds, id);
                }
            }
        });
        List<Exam> list = super.list(wrapper);
        list.sort((o1, o2) -> (int) (o1.getStartTime().getTime() - o2.getStartTime().getTime()));
        return MapperUtils.mapList(list, ExamVo.class);
    }

    @Override
    public PagingVo<FinishedExamVo> getFinishedExam(PagingDto pagingDto) {
        Long userId = JwtUtils.getUserId();
        List<FinishedExamVo> voList = Lists.newArrayList();
        List<Long> idList = departDubboService.getUserDepartId(userId);

        IPage<Exam> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();

        wrapper.eq(Exam::getState, ExamState.RELEASED)
                .le(Exam::getEndTime, new Date());

        wrapper.and(w -> {
            w.isNull(Exam::getDepartIds);
            if (!idList.isEmpty()) {
                for (Long id : idList) {
                    w.or().like(Exam::getDepartIds, id);
                }
            }
        });
        IPage<Exam> page = super.page(query, wrapper);
        for (Exam exam : page.getRecords()) {
            FinishedExamVo vo = new FinishedExamVo();
            MapperUtils.map(exam, vo);

            PaperRecordDto paperRecord = paperDubboService.getPaperRecord(exam.getPaperId(), userId);
            vo.setScore(paperRecord == null ? 0 : paperRecord.getScore());
            voList.add(vo);
        }

        return PagingVo.of(voList, page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public ExamVo create(ExamCreateRequest createRequest) {
        Exam exam = new Exam();

        MapperUtils.map(createRequest, exam);

        exam.setId(SnowFlakeIdWorker.nextId());
        exam.setCreator(JwtUtils.getUserId());
        exam.setDepartIds(Joiner.on(",").join(createRequest.getDepartIdList()));
        Date now = new Date();
        exam.setCreateTime(now);
        exam.setUpdateTime(now);

        super.save(exam);

        ExamVo examVo = new ExamVo();
        MapperUtils.map(exam, examVo);

        return examVo;
    }

    @Override
    public Exam getExamByIdInner(Long examId) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getId, examId);
        return super.getOne(wrapper, false);
    }

    @Override
    public ExamVo save(ExamSaveRequest saveRequest) {
        Exam exam = getExamByIdInner(saveRequest.getId());

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        Assert.isFalse(exam.getPaperId() != null && exam.getPaperId() > 0, ErrorCode.EXAM_PAPER_ALREADY_GENERATED);

        MapperUtils.map(saveRequest, exam);

        exam.setUpdateTime(new Date());
        exam.setDepartIds(Joiner.on(",").join(saveRequest.getDepartIdList()));
        super.updateById(exam);

        ExamVo examVo = new ExamVo();
        MapperUtils.map(exam, examVo);

        return examVo;
    }

    @Override
    public ExamVo info(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);

        return MapperUtils.map(exam, ExamVo.class);
    }

    @Override
    public ExamDetailVo detail(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);

        ExamDetailVo detailVo = new ExamDetailVo();
        MapperUtils.map(exam, detailVo);
        List<ExamQuestionVo> questionVos = Lists.newArrayList();
        if (PaperJoinType.MANUAL.equals(exam.getJoinType())) {
            questionVos.addAll(manualService.allManuals(examId));
        } else if (PaperJoinType.INTELLIGENT.equals(exam.getJoinType())) {
            questionVos.addAll(intelligentService.allIntelligents(examId));
        }
        detailVo.setQuestions(questionVos);
        return detailVo;
    }

    @Override
    public Boolean release(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        Assert.isTrue(ExamState.UNRELEASED.equals(exam.getState()), ErrorCode.EXAM_ALREADY_RELEASED);
        Assert.isTrue(exam.getPaperId() != null && exam.getPaperId() > 0, ErrorCode.EXAM_PAPER_NEED_GENERATE);

        LambdaUpdateWrapper<Exam> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Exam::getId, examId)
                .set(Exam::getState, ExamState.RELEASED);

        //提前三分钟推送通知
        long time = exam.getStartTime().getTime() - 3 * 60 * 1000L;
        cacheUtils.setExpireAt(RedisKeyConstants.EXAM_START_TIME + exam.getId(), exam.getId(), new Date(time));

        return super.update(wrapper);
    }

    @Override
    public Boolean delete(Long examId) {
        LambdaQueryWrapper<Exam> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Exam::getId, examId);

        taskExecutor.execute(() -> {
            LambdaQueryWrapper<ExamQuestionManual> manualWrapper = new LambdaQueryWrapper<>();
            manualWrapper.eq(ExamQuestionManual::getExamId, examId);
            manualService.remove(manualWrapper);

            LambdaQueryWrapper<ExamQuestionIntelligent> intelligentWrapper = new LambdaQueryWrapper<>();
            intelligentWrapper.eq(ExamQuestionIntelligent::getExamId, examId);
            intelligentService.remove(intelligentWrapper);

            cacheUtils.delete(RedisKeyConstants.EXAM_START_TIME + examId);
        });

        return super.remove(wrapper);
    }

    @Override
    public void pushPrestart(Long examId) {
        if (!cacheUtils.setIfAbsent(RedisKeyConstants.EXAM_START_TIME + examId, true, 3, TimeUnit.MINUTES)) {
            return;
        }
        Exam exam = getExamByIdInner(examId);
        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        cacheUtils.delete(RedisKeyConstants.EXAM_START_TIME + examId);
    }

    @Override
    public Long generatePaper(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        Assert.isFalse(exam.getPaperId() != null && exam.getPaperId() > 0, ErrorCode.EXAM_PAPER_ALREADY_GENERATED);

        PaperSettingDto paperSetting = new PaperSettingDto();
        paperSetting.setTitle(exam.getTitle());
        paperSetting.setTimeLimit(exam.getTimeLimit());
        paperSetting.setFaceCheck(exam.getFaceCheck());
        paperSetting.setJoinLimit(exam.getJoinLimit());
        paperSetting.setSubmitLimit(exam.getSubmitLimit());
        paperSetting.setStartTime(exam.getStartTime().getTime());
        paperSetting.setEndTime(exam.getEndTime().getTime());

        Set<Long> participants = Sets.newHashSet();
        String str = exam.getDepartIds();
        if (StringUtils.isNotBlank(str)) {
            if (str.contains(",")) {
                for (String s : str.split(",")) {
                    Long id = Long.parseLong(s);
                    participants.addAll(departDubboService.getMemberIds(id));
                }
            } else {
                Long id = Long.parseLong(str);
                participants.addAll(departDubboService.getMemberIds(id));
            }
        }
        paperSetting.setParticipants(Lists.newArrayList(participants));

        Long paperId;
        int totalScore = 0;
        if (PaperJoinType.MANUAL.equals(exam.getJoinType())) {
            List<QuestionManualDto> settings = Lists.newArrayList();
            for (ExamQuestionManual examQuestionManual : manualService.allManualsInner(examId)) {
                settings.add(MapperUtils.map(examQuestionManual, QuestionManualDto.class));
                totalScore += examQuestionManual.getScore();
            }
            paperId = paperDubboService.joinManual(paperSetting, settings);
        } else {
            List<QuestionIntelligentDto> settings = Lists.newArrayList();
            for (ExamQuestionIntelligent examQuestionIntelligent : intelligentService.allIntelligentsInner(examId)) {
                settings.add(MapperUtils.map(examQuestionIntelligent, QuestionIntelligentDto.class));
                totalScore += examQuestionIntelligent.getScore() * examQuestionIntelligent.getAmount();
            }
            paperId = paperDubboService.joinIntelligent(paperSetting, settings);
        }
        exam.setPaperId(paperId);
        exam.setTotalScore(totalScore);
        exam.setUpdateTime(new Date());
        super.updateById(exam);
        return paperId;
    }

    @Override
    public Long duplicatePaper(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        Assert.isTrue(exam.getPaperId() != null && exam.getPaperId() > 0, ErrorCode.PAPER_NOT_FOUND);

        return paperDubboService.duplicate(exam.getPaperId());
    }

    @Override
    public Boolean deletePaper(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);
        Assert.isTrue(exam.getPaperId() != null && exam.getPaperId() > 0, ErrorCode.PAPER_NOT_FOUND);

        exam.setPaperId(0L);
        exam.setUpdateTime(new Date());
        super.updateById(exam);

        return paperDubboService.delete(exam.getPaperId());
    }

    @Override
    public ExamAnalysisVo analysis(Long examId) {
        Exam exam = getExamByIdInner(examId);

        Assert.notNull(exam, ErrorCode.EXAM_NOT_FOUND);

        String str = exam.getDepartIds();
        List<Long> departIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(str)) {
            if (str.contains(",")) {
                for (String s : str.split(",")) {
                    departIds.add(Long.parseLong(s));
                }
            } else {
                departIds.add(Long.parseLong(str));
            }
        }
        ExamAnalysisVo analysisVo = new ExamAnalysisVo();
        analysisVo.setDepartAnalysis(Lists.newArrayList());
        Map<String, DepartMembersDto> classMap = departDubboService.getMembersBatch(departIds);
        List<PaperRecordDto> records = paperDubboService.getPaperRecords(exam.getPaperId());
        doAnalysis(analysisVo, records, exam.getTotalScore());
        for (Map.Entry<String, DepartMembersDto> entry : classMap.entrySet()) {
            ExamAnalysisVo classAnalysisVo = new ExamAnalysisVo();
            classAnalysisVo.setName(entry.getKey());
            analysisVo.getDepartAnalysis().add(classAnalysisVo);
            List<PaperRecordDto> classRecords = records.stream().filter(record -> entry.getValue().getMembers().contains(record.getUserId())).collect(Collectors.toList());
            doAnalysis(classAnalysisVo, classRecords, exam.getTotalScore());
        }

        return analysisVo;
    }

    private static final DecimalFormat AVERAGE_SCORE_FORMAT = new DecimalFormat("0.00");
    private static final DecimalFormat PASS_RATE_FORMAT = new DecimalFormat("0.00%");

    private void doAnalysis(ExamAnalysisVo analysisVo, List<PaperRecordDto> records, int totalScore) {
        analysisVo.setAttendNum(0);
        analysisVo.setAbsentNum(0);
        analysisVo.setLowestScore(0);
        analysisVo.setHighestScore(0);
        float total = 0.0F;
        float passed = 0.0F;
        for (PaperRecordDto record : records) {
            if (PaperRecordState.PREPARED.equals(record.getState())) {
                analysisVo.setAbsentNum(analysisVo.getAbsentNum() + 1);
            } else {
                analysisVo.setAttendNum(analysisVo.getAttendNum() + 1);
            }
            analysisVo.setLowestScore(Math.min(analysisVo.getLowestScore(), record.getScore()));
            analysisVo.setHighestScore(Math.max(analysisVo.getHighestScore(), record.getScore()));
            total += record.getScore();
            if (record.getScore() >= totalScore * 0.6F) {
                passed += 1.0F;
            }
        }
        analysisVo.setAverageScore(records.size() == 0 ? "0.00" : AVERAGE_SCORE_FORMAT.format(total / records.size()));
        analysisVo.setPassRate(records.size() == 0 ? "0.00%" : PASS_RATE_FORMAT.format(passed / records.size()));
    }

}
