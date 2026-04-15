package com.sdut.examonline.question.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.util.AesUtils;
import com.sdut.examonline.common.util.Assert;
import com.sdut.examonline.common.util.SnowFlakeIdWorker;
import com.sdut.examonline.question.constant.QuestionRepoType;
import com.sdut.examonline.question.dto.QuestionRepoCreateRequest;
import com.sdut.examonline.question.dto.QuestionRepoSaveRequest;
import com.sdut.examonline.question.dto.QuestionRepoSearchFilter;
import com.sdut.examonline.question.dto.QuestionRepoVerifyRequest;
import com.sdut.examonline.question.entity.Question;
import com.sdut.examonline.question.entity.QuestionRepo;
import com.sdut.examonline.question.mapper.QuestionRepoMapper;
import com.sdut.examonline.question.service.QuestionRepoService;
import com.sdut.examonline.question.service.QuestionService;
import com.sdut.examonline.question.vo.QuestionRepoVo;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class QuestionRepoServiceImpl extends ServiceImpl<QuestionRepoMapper, QuestionRepo> implements QuestionRepoService {

    private static final String KEY_QUESTION_REPO_VERFIY = "qrepo:verify:";

    @Autowired
    @Qualifier("asyncExecutor")
    ThreadPoolTaskExecutor taskExecutor;

    @Autowired
    QuestionService questionService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    CacheUtils cacheUtils;

    @Override
    public List<QuestionRepoVo> all() {
        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRepo::getCreator, JwtUtils.getUserId())
                .eq(QuestionRepo::getType, QuestionRepoType.PRIVATE);
        return MapperUtils.mapList(super.list(wrapper), QuestionRepoVo.class);
    }

    @Override
    public List<QuestionRepoVo> allPublic() {
        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRepo::getType, QuestionRepoType.PUBLIC);
        return MapperUtils.mapList(super.list(wrapper), QuestionRepoVo.class);
    }

    @Override
    public PagingVo<QuestionRepoVo> page(PagingDto pagingDto) {
        IPage<QuestionRepo> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRepo::getCreator, JwtUtils.getUserId())
                .eq(QuestionRepo::getType, QuestionRepoType.PRIVATE);
        IPage<QuestionRepo> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), QuestionRepoVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public PagingVo<QuestionRepoVo> pagePublic(PagingDto pagingDto) {
        IPage<QuestionRepo> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRepo::getType, QuestionRepoType.PUBLIC);
        IPage<QuestionRepo> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), QuestionRepoVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public PagingVo<QuestionVo> questions(PagingParamDto<Long> pagingParamDto) {
        Assert.isFalse(needVerify(pagingParamDto.getParam()), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        IPage<Question> query = new Page<>(pagingParamDto.getPageNum(), pagingParamDto.getPageSize());
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getRepoId, pagingParamDto.getParam());
        IPage<Question> page = questionService.page(query, wrapper);
        List<QuestionVo> records = Lists.newArrayList();
        for (Question record : page.getRecords()) {
            records.add(questionService.detail(record.getId()));
        }
        return PagingVo.of(records, page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public List<QuestionVo> questions(Long repoId) {
        Assert.isFalse(needVerify(repoId), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getRepoId, repoId);
        return MapperUtils.mapList(questionService.list(wrapper), QuestionVo.class);
    }

    @Override
    public QuestionVo duplicateFromRepo(Long questionId, Long destRepoId) {
        LambdaQueryWrapper<Question> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Question::getId, questionId);
        Question question = questionService.getOne(wrapper, false);

        Assert.notNull(question, ErrorCode.QUESTION_NOT_FOUND);

        Assert.isTrue(question.getParentId() == 0, ErrorCode.QUESTION_DUPLICATE_FROM_SUB);

        LambdaQueryWrapper<QuestionRepo> repoWrapper = new LambdaQueryWrapper<>();
        repoWrapper.eq(QuestionRepo::getId, destRepoId);
        QuestionRepo repo = super.getOne(repoWrapper, false);

        Assert.isFalse(needVerify(repo.getId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        Assert.notNull(repo, ErrorCode.QUESTION_REPO_NOT_FOUND);
        Assert.isTrue(JwtUtils.getUserId().equals(repo.getCreator()), ErrorCode.FORBIDDEN);

        LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
        questionWrapper.eq(Question::getParentId, question.getParentId());
        List<Question> list = questionService.list(questionWrapper);

        Date now = new Date();

        question.setId(SnowFlakeIdWorker.nextId());
        question.setRepoId(destRepoId);
        question.setUpdateTime(now);
        question.setCreateTime(now);

        for (Question q : list) {
            q.setId(SnowFlakeIdWorker.nextId());
            q.setRepoId(0L);
            q.setParentId(question.getId());
            q.setUpdateTime(now);
            q.setCreateTime(now);
        }

        list.add(question);

        questionService.saveBatch(list);

        return MapperUtils.map(question, QuestionVo.class);
    }

    @Override
    public QuestionRepoVo create(QuestionRepoCreateRequest createRequest) {
        QuestionRepo repo = new QuestionRepo();
        MapperUtils.map(createRequest, repo);
        repo.setId(SnowFlakeIdWorker.nextId());
        repo.setCreator(JwtUtils.getUserId());
        if (StringUtils.isNotEmpty(createRequest.getPassword())) {
            String decrypted = AesUtils.decrypt(createRequest.getPassword());
            Assert.notNull(decrypted, ErrorCode.USER_PASSWORD_NOT_MATCH);
            repo.setPassword(passwordEncoder.encode(decrypted));
        }
        Date now = new Date();
        repo.setUpdateTime(now);
        repo.setCreateTime(now);
        super.save(repo);

        QuestionRepoVo questionRepoVo = new QuestionRepoVo();
        MapperUtils.map(repo, questionRepoVo);

        return questionRepoVo;
    }

    @Override
    public QuestionRepoVo save(QuestionRepoSaveRequest saveRequest) {
        Assert.isFalse(needVerify(saveRequest.getId()), ErrorCode.QUESTION_REPO_NEED_VERIFY);
        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionRepo::getId, saveRequest.getId());
        QuestionRepo repo = super.getOne(wrapper, false);

        Assert.notNull(repo, ErrorCode.QUESTION_REPO_NOT_FOUND);

        if (StringUtils.isNotEmpty(repo.getPassword())) {
            String decrypted = AesUtils.decrypt(saveRequest.getNewPassword());
            Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");
            Assert.isTrue(passwordEncoder.matches(decrypted, repo.getPassword()), ErrorCode.QUESTION_REPO_PASSWORD_NOT_MATCH);
            repo.setPassword(passwordEncoder.encode(decrypted));
        } else {
            String decrypted = AesUtils.decrypt(saveRequest.getNewPassword());
            Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");
            repo.setPassword(passwordEncoder.encode(decrypted));
        }

        MapperUtils.map(saveRequest, repo);
        repo.setUpdateTime(new Date());
        super.updateById(repo);

        QuestionRepoVo questionRepoVo = new QuestionRepoVo();
        MapperUtils.map(repo, questionRepoVo);

        return questionRepoVo;
    }

    @Override
    public PagingVo<QuestionRepoVo> search(PagingParamDto<QuestionRepoSearchFilter> paramDto) {
        QuestionRepoSearchFilter searchFilter = paramDto.getParam();

        Assert.notNull(searchFilter, ErrorCode.BAD_REQUEST);

        IPage<QuestionRepo> query = new Page<>(paramDto.getPageNum(), paramDto.getPageSize());

        LambdaQueryWrapper<QuestionRepo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(searchFilter.getName())) {
            wrapper.like(QuestionRepo::getName, searchFilter.getName());
        }
        if (searchFilter.getType() != null && searchFilter.getType() >= 0) {
            wrapper.eq(QuestionRepo::getType, searchFilter.getType());
        }
        IPage<QuestionRepo> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), QuestionRepoVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public Boolean needVerify(Long repoId) {
        LambdaQueryWrapper<QuestionRepo> repoWrapper = new LambdaQueryWrapper<>();
        repoWrapper.eq(QuestionRepo::getId, repoId);
        QuestionRepo repo = super.getOne(repoWrapper, false);
        Assert.notNull(repo, ErrorCode.QUESTION_REPO_NOT_FOUND);

        if (StringUtils.isEmpty(repo.getPassword())) {
            return false;
        }

        String accessToken = JwtUtils.getAccessToken();
        String verified = cacheUtils.getStringNullable(getRedisVerifyKey(repoId, accessToken), 10L, TimeUnit.MINUTES);
        return StringUtils.isEmpty(verified) || !"verified".equalsIgnoreCase(verified);
    }

    @Override
    public Boolean verify(QuestionRepoVerifyRequest request) {
        LambdaQueryWrapper<QuestionRepo> repoWrapper = new LambdaQueryWrapper<>();
        repoWrapper.eq(QuestionRepo::getId, request.getId());
        QuestionRepo repo = super.getOne(repoWrapper, false);
        Assert.notNull(repo, ErrorCode.QUESTION_REPO_NOT_FOUND);

        if (StringUtils.isEmpty(repo.getPassword())) {
            return true;
        }

        String decrypted = AesUtils.decrypt(request.getPassword());
        Assert.isTrue(decrypted != null && decrypted.length() >= 5 && decrypted.length() <= 20, 400, "密码长度为5-20之间");

        Assert.isTrue(passwordEncoder.matches(decrypted, repo.getPassword()), ErrorCode.QUESTION_REPO_PASSWORD_NOT_MATCH);

        cacheUtils.setString(getRedisVerifyKey(request.getId(), JwtUtils.getAccessToken()), "verified", 10L, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public Boolean delete(Long repoId) {
        Assert.isFalse(needVerify(repoId), ErrorCode.QUESTION_REPO_NEED_VERIFY);

        LambdaQueryWrapper<QuestionRepo> repoWrapper = new LambdaQueryWrapper<>();
        repoWrapper.eq(QuestionRepo::getId, repoId);
        QuestionRepo questionRepo = super.getOne(repoWrapper, false);
        Assert.notNull(questionRepo, ErrorCode.QUESTION_REPO_NOT_FOUND);
        Assert.isTrue(questionRepo.getCreator().equals(JwtUtils.getUserId()), ErrorCode.FORBIDDEN);

        taskExecutor.execute(() -> {
            LambdaQueryWrapper<Question> questionWrapper = new LambdaQueryWrapper<>();
            questionWrapper.eq(Question::getRepoId, repoId);
            questionService.remove(questionWrapper);
        });

        return super.remove(repoWrapper);
    }

    private String getRedisVerifyKey(Long repoId, String accessToken) {
        return KEY_QUESTION_REPO_VERFIY + repoId + ":" + accessToken;
    }

}
