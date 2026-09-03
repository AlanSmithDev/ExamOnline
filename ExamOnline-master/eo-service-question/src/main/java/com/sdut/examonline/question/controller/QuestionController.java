package com.sdut.examonline.question.controller;

import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.question.dto.*;
import com.sdut.examonline.question.service.QuestionAnswerService;
import com.sdut.examonline.question.service.QuestionRepoService;
import com.sdut.examonline.question.service.QuestionService;
import com.sdut.examonline.question.vo.QuestionAnswerVo;
import com.sdut.examonline.question.vo.QuestionRepoVo;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Api("题库管理")
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    QuestionAnswerService questionAnswerService;

    @Autowired
    QuestionRepoService questionRepoService;
    private Thread thread;

    @ApiOperation(value = "填充测试数据")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/fillTestData/{repoId}", method = RequestMethod.GET)
    ResponseVo<Void> fillTestData(@PathVariable("repoId") Long repoId) {
        questionService.fillTestData(repoId);
        return ResponseVo.success();
    }

    @ApiOperation(value = "填充100个题库")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/fillRepoData", method = RequestMethod.GET)
    ResponseVo<Void> fillRepoData() {
        if (thread != null) {
            return ResponseVo.failure();
        }
        List<QuestionRepoVo> repoList = Lists.newArrayList();
        for (int i = 0; i < 100; i++) {
            QuestionRepoCreateRequest request = new QuestionRepoCreateRequest();
            request.setName("测试题库" + (i + 1));
            request.setType(0);
            QuestionRepoVo repoVo = questionRepoService.create(request);
            repoList.add(repoVo);
        }
        thread = new Thread(() -> {
            for (QuestionRepoVo repoVo : repoList) {
                questionService.fillTestData(repoVo.getId(), 3953496957202433L);
            }
        });
        thread.start();
        return ResponseVo.success();
    }

    @LogOperation(msg = "获取题目详细信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取题目详细信息")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/detail/{questionId}", method = RequestMethod.GET)
    ResponseVo<QuestionVo> detail(@PathVariable("questionId") Long questionId) {
        return ResponseVo.success(questionService.detail(questionId));
    }

    @LogOperation(msg = "添加题目", type = OperationType.CREATE)
    @ApiOperation(value = "添加题目")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseVo<QuestionVo> add(@RequestBody QuestionCreateRequest createRequest) {
        return ResponseVo.success(questionService.add(createRequest));
    }

    @LogOperation(msg = "添加题目及预选答案", type = OperationType.CREATE)
    @ApiOperation(value = "添加题目及预选答案")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/addDetail", method = RequestMethod.POST)
    ResponseVo<QuestionVo> addDetail(@RequestBody QuestionDetailCreateRequest createRequest) {
        return ResponseVo.success(questionService.addDetail(createRequest));
    }

    @LogOperation(msg = "保存题目", type = OperationType.UPDATE)
    @ApiOperation(value = "保存题目")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    ResponseVo<QuestionVo> save(@RequestBody QuestionSaveRequest saveRequest) {
        return ResponseVo.success(questionService.save(saveRequest));
    }

    @LogOperation(msg = "分页搜索题目", type = OperationType.QUERY)
    @ApiOperation(value = "分页搜索题目")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseVo<PagingVo<QuestionVo>> search(@RequestBody PagingParamDto<QuestionSearchFilter> paramDto) {
        return ResponseVo.success(questionService.search(paramDto));
    }

    @LogOperation(msg = "删除题目", type = OperationType.DELETE)
    @ApiOperation(value = "删除题目")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/delete/{questionId}", method = RequestMethod.GET)
    ResponseVo<Boolean> delete(@PathVariable("questionId") Long questionId) {
        return ResponseVo.judge(questionService.delete(questionId));
    }

    @LogOperation(msg = "获取题目预选答案", type = OperationType.QUERY)
    @ApiOperation(value = "获取题目预选答案")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/answer/answers/{questionId}", method = RequestMethod.GET)
    ResponseVo<List<QuestionAnswerVo>> answers(@PathVariable("questionId") Long questionId) {
        return ResponseVo.success(questionAnswerService.getAnswersByQuestionId(questionId));
    }

    @LogOperation(msg = "新增预选答案", type = OperationType.CREATE)
    @ApiOperation(value = "新增预选答案")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/answer/add", method = RequestMethod.POST)
    ResponseVo<QuestionAnswerVo> addAnswer(@RequestBody QuestionAnswerCreateRequest createRequest) {
        return ResponseVo.success(questionAnswerService.addAnswer(createRequest));
    }

    @LogOperation(msg = "保存预选答案", type = OperationType.UPDATE)
    @ApiOperation(value = "保存预选答案")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/answer/save", method = RequestMethod.POST)
    ResponseVo<QuestionAnswerVo> saveAnswer(@RequestBody QuestionAnswerSaveRequest saveRequest) {
        return ResponseVo.success(questionAnswerService.saveAnswer(saveRequest));
    }

    @LogOperation(msg = "移除预选答案", type = OperationType.DELETE)
    @ApiOperation(value = "移除预选答案")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/answer/remove/{answerId}", method = RequestMethod.GET)
    ResponseVo<Boolean> removeAnswer(@PathVariable("answerId") Long answerId) {
        return ResponseVo.judge(questionAnswerService.removeAnswer(answerId));
    }

    @AccessLogIgnored
    @LogOperation(msg = "从Excel导入试题", type = OperationType.IMPORT)
    @ApiOperation(value = "从Excel导入试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/import/{repoId}", method = RequestMethod.POST)
    ResponseVo<List<QuestionVo>> importFromExcel(@PathVariable("repoId") Long repoId, @RequestParam("file") MultipartFile file) throws Exception {
        return ResponseVo.success(questionService.importFromExcel(repoId, file.getInputStream()));
    }

    @AccessLogIgnored
    @LogOperation(msg = "下载导入模板", type = OperationType.QUERY)
    @ApiOperation(value = "下载导入模板")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/example", method = RequestMethod.GET)
    void example(HttpServletResponse response) throws Exception {
        String fileName = "template.xls";
        ClassPathResource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            log.info("导入模板文件(template.xls)不存在");
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(resource.getInputStream().available()));
        IOUtils.copy(resource.getInputStream(), response.getOutputStream(), 24);
    }

}
