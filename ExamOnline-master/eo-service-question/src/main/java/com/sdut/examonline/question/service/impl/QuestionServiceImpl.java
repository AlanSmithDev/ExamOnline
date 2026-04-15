package com.sdut.examonline.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.QuestionType;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.dubbo.dto.QuestionIntelligentDto;
import com.sdut.examonline.question.dto.*;
import com.sdut.examonline.question.entity.Question;
import com.sdut.examonline.question.entity.QuestionAnswer;
import com.sdut.examonline.question.mapper.QuestionMapper;
import com.sdut.examonline.question.service.QuestionAnswerService;
import com.sdut.examonline.question.service.QuestionRepoService;
import com.sdut.examonline.question.service.QuestionService;
import com.sdut.examonline.question.vo.QuestionAnswerVo;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.ExcelUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    CacheUtils cacheUtils;

    @Autowired
    QuestionAnswerService questionAnswerService;

    @Autowired
    QuestionRepoService questionRepoService;

    @Override
    public void fillTestData(Long repoId) {
        fillTestData(repoId, JwtUtils.getUserId());
    }

    @Override
    public void fillTestData(Long repoId, Long userId) {
        long startTime = System.currentTimeMillis();
        Date now = new Date();
        List<Question> questions = Lists.newArrayList();
        List<QuestionAnswer> answers = Lists.newArrayList();
        for (int i = 0; i < 1000; i++) {
            Question question = new Question();
            question.setId(SnowFlakeIdWorker.nextId());
            question.setType(RandomUtils.nextInt(0, 8));
            question.setDifficulty(RandomUtils.nextInt(1, 6));
            question.setRepoId(repoId);
            question.setCreator(userId);
            question.setAnalysis("解析内容");
            question.setUpdateTime(now);
            question.setCreateTime(now);

            if (QuestionType.SINGLE_CHOICE.equals(question.getType())) {
                int rightIdx = RandomUtils.nextInt(0, 3);
                String text = "";
                for (int j = 0; j < 4; j++) {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setId(SnowFlakeIdWorker.nextId());
                    answer.setContent("选项" + (j + 1));
                    answer.setIsRight(j == rightIdx);
                    if (answer.getIsRight()) {
                        text = answer.getContent();
                    }
                    answer.setQuestionId(question.getId());
                    answer.setUpdateTime(now);
                    answer.setCreateTime(now);
                    answers.add(answer);
                }
                question.setContent("本题选择: " + text);
            } else if (QuestionType.MULTIPLE_CHOICE.equals(question.getType())) {
                boolean flag = false;
                List<String> options = Lists.newArrayList();
                for (int j = 0; j < 4; j++) {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setId(SnowFlakeIdWorker.nextId());
                    answer.setContent("选项" + (j + 1));
                    answer.setIsRight(RandomUtils.nextBoolean());
                    if (answer.getIsRight()) {
                        flag = true;
                        options.add(answer.getContent());
                    }
                    if (j == 3 && !flag) {
                        flag = true;
                        answer.setIsRight(true);
                        options.add(answer.getContent());
                    }
                    answer.setQuestionId(question.getId());
                    answer.setUpdateTime(now);
                    answer.setCreateTime(now);
                    answers.add(answer);
                }
                question.setContent("本题选择: " + ArrayUtils.toString(options));
            } else if (QuestionType.JUDGEMENT.equals(question.getType())) {
                QuestionAnswer answer = new QuestionAnswer();
                answer.setId(SnowFlakeIdWorker.nextId());
                answer.setIsRight(RandomUtils.nextBoolean());
                answer.setQuestionId(question.getId());
                answer.setUpdateTime(now);
                answer.setCreateTime(now);
                answers.add(answer);
                question.setContent("本题答案: " + (answer.getIsRight() ? "正确" : "错误"));
            } else if (QuestionType.COMPLETION.equals(question.getType())) {
                int len = RandomUtils.nextInt(1, 6);
                List<String> options = Lists.newArrayList();
                for (int j = 0; j < len; j++) {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setId(SnowFlakeIdWorker.nextId());
                    answer.setContent("空" + (j + 1));
                    answer.setQuestionId(question.getId());
                    answer.setUpdateTime(now);
                    answer.setCreateTime(now);
                    answers.add(answer);
                    options.add(answer.getContent());
                }
                question.setContent("本题答案: " + ArrayUtils.toString(options));
            } else if (QuestionType.CLOZE.equals(question.getType())) {
                question.setContent("完形填空题");
                question.setAnalysis(null);
                int len = RandomUtils.nextInt(15, 20);
                for (int k = 0; k < len; k++) {
                    Question subQuestion = new Question();
                    subQuestion.setId(SnowFlakeIdWorker.nextId());
                    subQuestion.setType(QuestionType.SINGLE_CHOICE);
                    subQuestion.setDifficulty(RandomUtils.nextInt(1, 6));
                    subQuestion.setRepoId(0L);
                    subQuestion.setParentId(question.getId());
                    subQuestion.setCreator(userId);
                    subQuestion.setAnalysis("小题解析内容");
                    subQuestion.setUpdateTime(now);
                    subQuestion.setCreateTime(now);
                    int rightIdx = RandomUtils.nextInt(0, 3);
                    String text = "";
                    for (int j = 0; j < 4; j++) {
                        QuestionAnswer answer = new QuestionAnswer();
                        answer.setId(SnowFlakeIdWorker.nextId());
                        answer.setContent("选项" + (j + 1));
                        answer.setIsRight(j == rightIdx);
                        if (answer.getIsRight()) {
                            text = answer.getContent();
                        }
                        answer.setQuestionId(subQuestion.getId());
                        answer.setUpdateTime(now);
                        answer.setCreateTime(now);
                        answers.add(answer);
                    }
                    subQuestion.setContent("本题选择: " + text);
                    questions.add(subQuestion);
                }
            } else if (QuestionType.READING_COMPREHENSION.equals(question.getType())) {
                question.setContent("阅读理解题");
                question.setAnalysis(null);
                int len = RandomUtils.nextInt(3, 5);
                for (int k = 0; k < len; k++) {
                    Question subQuestion = new Question();
                    subQuestion.setId(SnowFlakeIdWorker.nextId());
                    subQuestion.setType(QuestionType.SINGLE_CHOICE);
                    subQuestion.setDifficulty(RandomUtils.nextInt(1, 6));
                    subQuestion.setRepoId(0L);
                    subQuestion.setParentId(question.getId());
                    subQuestion.setCreator(userId);
                    subQuestion.setAnalysis("小题解析内容");
                    subQuestion.setUpdateTime(now);
                    subQuestion.setCreateTime(now);
                    int rightIdx = RandomUtils.nextInt(0, 3);
                    String text = "";
                    for (int j = 0; j < 4; j++) {
                        QuestionAnswer answer = new QuestionAnswer();
                        answer.setId(SnowFlakeIdWorker.nextId());
                        answer.setContent("选项" + (j + 1));
                        answer.setIsRight(j == rightIdx);
                        if (answer.getIsRight()) {
                            text = answer.getContent();
                        }
                        answer.setQuestionId(subQuestion.getId());
                        answer.setUpdateTime(now);
                        answer.setCreateTime(now);
                        answers.add(answer);
                    }
                    subQuestion.setContent("本题选择: " + text);
                    questions.add(subQuestion);
                }
            } else if (QuestionType.LISTENING.equals(question.getType())) {
                question.setContent("听力题");
                question.setAnalysis(null);
                int len = RandomUtils.nextInt(3, 5);
                for (int k = 0; k < len; k++) {
                    Question subQuestion = new Question();
                    subQuestion.setId(SnowFlakeIdWorker.nextId());
                    subQuestion.setType(QuestionType.SINGLE_CHOICE);
                    subQuestion.setDifficulty(RandomUtils.nextInt(1, 6));
                    subQuestion.setRepoId(0L);
                    subQuestion.setParentId(question.getId());
                    subQuestion.setCreator(userId);
                    subQuestion.setAnalysis("小题解析内容");
                    subQuestion.setUpdateTime(now);
                    subQuestion.setCreateTime(now);
                    int rightIdx = RandomUtils.nextInt(0, 3);
                    String text = "";
                    for (int j = 0; j < 4; j++) {
                        QuestionAnswer answer = new QuestionAnswer();
                        answer.setId(SnowFlakeIdWorker.nextId());
                        answer.setContent("选项" + (j + 1));
                        answer.setIsRight(j == rightIdx);
                        if (answer.getIsRight()) {
                            text = answer.getContent();
                        }
                        answer.setQuestionId(subQuestion.getId());
                        answer.setUpdateTime(now);
                        answer.setCreateTime(now);
                        answers.add(answer);
                    }
                    subQuestion.setContent("本题选择: " + text);
                    questions.add(subQuestion);
                }
            }
            questions.add(question);
        }
        super.saveBatch(questions);
        questionAnswerService.saveBatch(answers);
        System.out.println("填充完成，耗时" + (System.currentTimeMillis() - startTime) + "ms!");
    }

    @Override
    public QuestionVo detail(Long questionId) {
        return getQuestionDetailInner(questionId, () -> {
            Question question = getQuestionInner(questionId);
            Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);

            QuestionVo questionVo = new QuestionVo();
            MapperUtils.map(question, questionVo);

            questionVo.setAnswers(questionAnswerService.getAnswersByQuestionId(questionId));
            if (QuestionType.hasSubQuestions(question.getType()) && question.getRepoId() != 0L) {
                questionVo.setSubQuestions(getSubQuestions(question.getId()));
            } else {
                questionVo.setSubQuestions(Collections.emptyList());
            }
            return questionVo;
        });
    }

    private List<QuestionVo> getSubQuestions(Long questionId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getParentId, questionId);
        List<Question> list = super.list(wrapper);
        List<QuestionVo> voList = Lists.newArrayList();
        for (Question question : list) {
            QuestionVo questionVo = new QuestionVo();
            MapperUtils.map(question, questionVo);
            questionVo.setAnswers(questionAnswerService.getAnswersByQuestionId(question.getId()));
            questionVo.setSubQuestions(Collections.emptyList());
            voList.add(questionVo);
        }
        return voList;
    }

    @Override
    public QuestionVo add(QuestionCreateRequest createRequest) {
        Assert.isFalse(questionRepoService.needVerify(createRequest.getRepoId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);
        Question question = new Question();
        MapperUtils.map(createRequest, question);
        question.setId(SnowFlakeIdWorker.nextId());
        question.setCreator(JwtUtils.getUserId());

        if (createRequest.getParentId() != null && createRequest.getParentId() > 0) {
            createRequest.setRepoId(0L);
        }

        Date now = new Date();
        question.setUpdateTime(now);
        question.setCreateTime(now);

        super.save(question);

        return MapperUtils.map(question, QuestionVo.class);
    }

    @Override
    public QuestionVo addDetail(QuestionDetailCreateRequest createRequest) {
        Assert.isFalse(questionRepoService.needVerify(createRequest.getRepoId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);
        Date now = new Date();
        Long userId = JwtUtils.getUserId();

        List<Question> questionToSave = Lists.newArrayList();
        List<QuestionAnswer> questionAnswerToSave = Lists.newArrayList();

        Question question = new Question();
        MapperUtils.map(createRequest, question);
        question.setId(SnowFlakeIdWorker.nextId());
        question.setCreator(userId);

        question.setUpdateTime(now);
        question.setCreateTime(now);
        questionToSave.add(question);

        if (CollectionUtils.isNotEmpty(createRequest.getAnswers())) {
            for (QuestionDetailCreateRequest.QuestionAnswerInfo questionAnswerInfo : createRequest.getAnswers()) {
                QuestionAnswer answer = new QuestionAnswer();
                answer.setId(SnowFlakeIdWorker.nextId());
                answer.setContent(questionAnswerInfo.getContent());
                answer.setIsRight(questionAnswerInfo.getIsRight());
                answer.setQuestionId(question.getId());
                answer.setUpdateTime(now);
                answer.setCreateTime(now);
                questionAnswerToSave.add(answer);
            }
        }

        if (CollectionUtils.isNotEmpty(createRequest.getSubQuestions())) {
            for (QuestionDetailCreateRequest subCreateRequest : createRequest.getSubQuestions()) {
                Question subQuestion = new Question();
                MapperUtils.map(subCreateRequest, subQuestion);
                subQuestion.setId(SnowFlakeIdWorker.nextId());
                subQuestion.setParentId(question.getId());
                subQuestion.setRepoId(0L);
                subQuestion.setCreator(userId);

                subQuestion.setUpdateTime(now);
                subQuestion.setCreateTime(now);
                questionToSave.add(subQuestion);

                if (CollectionUtils.isNotEmpty(subCreateRequest.getAnswers())) {
                    for (QuestionDetailCreateRequest.QuestionAnswerInfo questionAnswerInfo : subCreateRequest.getAnswers()) {
                        QuestionAnswer answer = new QuestionAnswer();
                        answer.setId(SnowFlakeIdWorker.nextId());
                        answer.setContent(questionAnswerInfo.getContent());
                        answer.setIsRight(questionAnswerInfo.getIsRight());
                        answer.setQuestionId(subQuestion.getId());
                        answer.setUpdateTime(now);
                        answer.setCreateTime(now);
                        questionAnswerToSave.add(answer);
                    }
                }
            }
        }

        super.saveBatch(questionToSave);
        questionAnswerService.saveBatch(questionAnswerToSave);
        return MapperUtils.map(question, QuestionVo.class);
    }

    @Override
    public QuestionVo save(QuestionSaveRequest saveRequest) {
        Question question = getQuestionInner(saveRequest.getId());
        Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);

        Assert.isFalse(questionRepoService.needVerify(question.getRepoId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        MapperUtils.map(saveRequest, question);
        question.setUpdateTime(new Date());
        super.updateById(question);

        removeQuestionCache(question.getId());

        return MapperUtils.map(question, QuestionVo.class);
    }

    @Override
    public PagingVo<QuestionVo> search(PagingParamDto<QuestionSearchFilter> paramDto) {
        Assert.isFalse(questionRepoService.needVerify(paramDto.getParam().getRepoId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);
        QuestionSearchFilter searchFilter = paramDto.getParam();

        Assert.notNull(searchFilter, ErrorCode.BAD_REQUEST);

        IPage<Question> query = new Page<>(paramDto.getPageNum(), paramDto.getPageSize());

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getRepoId, searchFilter.getRepoId());

        if (StringUtils.isNotEmpty(searchFilter.getContent())) {
            wrapper.like(Question::getContent, searchFilter.getContent());
        }

        if (CollectionUtils.isNotEmpty(searchFilter.getTypes())) {
            wrapper.in(Question::getType, searchFilter.getTypes());
        }

        if (CollectionUtils.isNotEmpty(searchFilter.getDifficulties())) {
            wrapper.in(Question::getDifficulty, searchFilter.getDifficulties());
        }
        IPage<Question> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), QuestionVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public Question getQuestionByIdInner(Long questionId) {
        return getQuestionInner(questionId);
    }

    @Override
    public Boolean delete(Long questionId) {
        Question question = getQuestionInner(questionId);
        Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);
        Assert.isFalse(questionRepoService.needVerify(question.getRepoId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        removeQuestionCache(questionId);

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getId, questionId);
        boolean questionRemoved = super.remove(wrapper);

        wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getParentId, questionId);
        boolean subQuestionsRemoved = super.remove(wrapper);

        return questionRemoved || subQuestionsRemoved;
    }

    @Override
    public List<QuestionDto> randomGet(QuestionIntelligentDto setting) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getRepoId, setting.getRepoId())
                .eq(Question::getType, setting.getQuestionType());
        if (setting.getDifficulty() != null && setting.getDifficulty() != 0) {
            wrapper.eq(Question::getDifficulty, setting.getDifficulty());
        }

        List<Question> list = super.list(wrapper);
        if (list.size() <= setting.getAmount()) {
            return MapperUtils.mapList(list, QuestionDto.class);
        }
        while (list.size() > setting.getAmount()) {
            list.remove(RandomUtils.nextInt(0, list.size()));
        }
        List<QuestionDto> result = MapperUtils.mapList(list, QuestionDto.class);
        for (QuestionDto question : result) {
            if (QuestionType.hasSubQuestions(question.getType())) {
                question.setSubQuestions(MapperUtils.mapList(getSubQuestions(question.getId()), QuestionDto.class));
            }
        }
        return result;
    }

    @Override
    public List<QuestionVo> importFromExcel(Long repoId, InputStream inputStream) {
        Assert.isFalse(questionRepoService.needVerify(repoId), ErrorCode.QUESTION_REPO_NEED_VERIFY);
        Long userId = JwtUtils.getUserId();
        Date now = new Date();
        List<QuestionImportDto> list = ExcelUtils.readExcel(inputStream, QuestionImportDto.class);
        List<QuestionVo> questionVos = Lists.newArrayList();
        List<Question> questions = Lists.newArrayList();
        List<QuestionAnswer> answers = Lists.newArrayList();
        for (QuestionImportDto dto : list) {
            Question question = new Question();
            question.setId(SnowFlakeIdWorker.nextId());
            question.setType(dto.getType());
            question.setDifficulty(dto.getDifficulty());
            question.setContent(dto.getContent());
            question.setAnalysis(dto.getAnalysis());
            question.setRepoId(repoId);
            question.setCreator(userId);
            question.setUpdateTime(now);
            question.setCreateTime(now);

            if (QuestionType.SINGLE_CHOICE.equals(question.getType())) {
                String[] options = dto.getOptions().split("#");
                boolean hasCorrect = false;
                List<QuestionAnswer> answerList = Lists.newArrayList();
                for (String option : options) {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setId(SnowFlakeIdWorker.nextId());
                    answer.setContent(option);
                    answer.setIsRight(option.equals(dto.getCorrectOptions()));
                    if (answer.getIsRight()) {
                        hasCorrect = true;
                    }
                    answer.setQuestionId(question.getId());
                    answer.setUpdateTime(now);
                    answer.setCreateTime(now);
                    answerList.add(answer);
                }
                if (hasCorrect) {
                    questions.add(question);
                    answers.addAll(answerList);
                    QuestionVo questionVo = new QuestionVo();
                    MapperUtils.map(question, questionVo);
                    questionVo.setAnswers(MapperUtils.mapList(answerList, QuestionAnswerVo.class));
                    questionVos.add(questionVo);
                }
            } else if (QuestionType.MULTIPLE_CHOICE.equals(question.getType())) {
                String[] options = dto.getOptions().split("#");
                String[] correctOptions = dto.getCorrectOptions().split("#");
                int correct = 0;
                List<QuestionAnswer> answerList = Lists.newArrayList();
                for (String option : options) {
                    QuestionAnswer answer = new QuestionAnswer();
                    answer.setId(SnowFlakeIdWorker.nextId());
                    answer.setContent(option);
                    answer.setIsRight(ArrayUtils.contains(correctOptions, option));
                    if (answer.getIsRight()) {
                        correct++;
                    }
                    answer.setQuestionId(question.getId());
                    answer.setUpdateTime(now);
                    answer.setCreateTime(now);
                    answerList.add(answer);
                }
                if (correct > 1) {
                    questions.add(question);
                    answers.addAll(answerList);
                    QuestionVo questionVo = new QuestionVo();
                    MapperUtils.map(question, questionVo);
                    questionVo.setAnswers(MapperUtils.mapList(answerList, QuestionAnswerVo.class));
                    questionVos.add(questionVo);
                }
            } else if (QuestionType.JUDGEMENT.equals(question.getType())) {
                QuestionAnswer answer = new QuestionAnswer();
                answer.setId(SnowFlakeIdWorker.nextId());
                answer.setIsRight("正确".equals(dto.getCorrectOptions()));
                answer.setQuestionId(question.getId());
                answer.setUpdateTime(now);
                answer.setCreateTime(now);
                questions.add(question);
                answers.add(answer);
                QuestionVo questionVo = new QuestionVo();
                MapperUtils.map(question, questionVo);
                questionVo.setAnswers(Collections.singletonList(MapperUtils.map(answer, QuestionAnswerVo.class)));
                questionVos.add(questionVo);
            }
        }
        questionAnswerService.saveBatch(answers);
        super.saveBatch(questions);
        return questionVos;
    }

    private Question getQuestionInner(Long questionId) {
        Object value = cacheUtils.get(getCacheKeyQuestion(questionId), Question.class, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (cacheUtils.isEmptyObject(value)) {
            throw new ServiceException(ErrorCode.QUESTION_NOT_FOUND);
        }
        if (value != null) {
            return (Question) value;
        }
        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Question::getId, questionId);
        Question question = super.getOne(wrapper, false);
        if (question != null) {
            cacheUtils.set(getCacheKeyQuestion(question.getId()), question, 10, TimeUnit.MINUTES);
        }
        return question;
    }

    private QuestionVo getQuestionDetailInner(Long questionId, Supplier<QuestionVo> provider) {
        Object value = cacheUtils.getNullable(getCacheKeyQuestionDetail(questionId), QuestionVo.class, 10, TimeUnit.MINUTES);
        if (value != null) {
            return (QuestionVo) value;
        }
        QuestionVo questionVo = provider.get();
        if (questionVo != null) {
            cacheUtils.set(getCacheKeyQuestionDetail(questionId), questionVo, 10, TimeUnit.MINUTES);
        }
        return questionVo;
    }

    @Override
    public void removeQuestionCache(Long questionId) {
        cacheUtils.delete(getCacheKeyQuestion(questionId));
        cacheUtils.delete(getCacheKeyQuestionDetail(questionId));
    }

    private String getCacheKeyQuestion(Long questionId) {
        return "qu:id:" + questionId;
    }

    private String getCacheKeyQuestionDetail(Long questionId) {
        return "qu:detail:" + questionId;
    }

}
