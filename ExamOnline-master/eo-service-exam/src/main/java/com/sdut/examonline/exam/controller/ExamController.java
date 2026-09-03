package com.sdut.examonline.exam.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.exam.vo.*;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.exam.dto.*;
import com.sdut.examonline.exam.service.ExamQuestionIntelligentService;
import com.sdut.examonline.exam.service.ExamQuestionManualService;
import com.sdut.examonline.exam.service.ExamService;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("考试管理")
@RestController
@RequestMapping("/api/exam")
public class ExamController {

    @Autowired
    ExamService examService;

    @Autowired
    ExamQuestionManualService questionManualService;

    @Autowired
    ExamQuestionIntelligentService questionIntelligentService;

    @LogOperation(msg = "获取用户的考试列表", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户的考试列表(如展示给学生)")
    @RequestMapping(value = "/my", method = RequestMethod.POST)
    ResponseVo<PagingVo<ExamVo>> myExams(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(examService.myExams(pagingDto));
    }

    @LogOperation(msg = "获取用户创建的考试列表", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户创建的考试列表")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    ResponseVo<PagingVo<ExamVo>> list(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(examService.list(pagingDto));
    }

    @LogOperation(msg = "创建考试", type = OperationType.CREATE)
    @ApiOperation(value = "创建考试")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    ResponseVo<ExamVo> create(@RequestBody ExamCreateRequest createRequest) {
        return ResponseVo.success(examService.create(createRequest));
    }

    @LogOperation(msg = "保存考试", type = OperationType.UPDATE)
    @ApiOperation(value = "保存考试")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    ResponseVo<ExamVo> save(@RequestBody ExamSaveRequest saveRequest) {
        return ResponseVo.success(examService.save(saveRequest));
    }

    @LogOperation(msg = "获取考试信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取考试信息")
    @RequestMapping(value = "/info/{examId}", method = RequestMethod.GET)
    ResponseVo<ExamVo> info(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.info(examId));
    }

    @LogOperation(msg = "获取考试详情", type = OperationType.QUERY)
    @ApiOperation(value = "获取考试详情")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/detail/{examId}", method = RequestMethod.GET)
    ResponseVo<ExamDetailVo> detail(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.detail(examId));
    }

    @LogOperation(msg = "发布考试", type = OperationType.UPDATE)
    @ApiOperation(value = "发布考试")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/release/{examId}", method = RequestMethod.GET)
    ResponseVo<Boolean> release(@PathVariable("examId") Long examId) {
        return ResponseVo.judge(examService.release(examId));
    }

    @LogOperation(msg = "删除考试", type = OperationType.DELETE)
    @ApiOperation(value = "删除考试")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/delete/{examId}", method = RequestMethod.GET)
    ResponseVo<Boolean> delete(@PathVariable("examId") Long examId) {
        return ResponseVo.judge(examService.delete(examId));
    }

    @LogOperation(msg = "获取用户下即将开始的考试", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户下即将开始的考试")
    @RequestMapping(value = "/prestart", method = RequestMethod.GET)
    ResponseVo<List<ExamVo>> getPrestartExam() {
        return ResponseVo.success(examService.getPrestartExam());
    }

    @LogOperation(msg = "获取用户下正在进行的考试", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户下正在进行的考试")
    @RequestMapping(value = "/answering", method = RequestMethod.GET)
    ResponseVo<List<ExamVo>> getAnsweringExam() {
        return ResponseVo.success(examService.getAnsweringExam());
    }

    @LogOperation(msg = "获取用户下完成的考试", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户下完成的考试")
    @RequestMapping(value = "/finished", method = RequestMethod.POST)
    ResponseVo<PagingVo<FinishedExamVo>> getFinishedExam(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(examService.getFinishedExam(pagingDto));
    }

    @LogOperation(msg = "新增手动组卷试题", type = OperationType.CREATE)
    @ApiOperation(value = "新增手动组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/manual/add", method = RequestMethod.POST)
    ResponseVo<ExamQuestionManualVo> add(@RequestBody ExamQuestionManualAddRequest addRequest) {
        return ResponseVo.success(questionManualService.addManual(addRequest));
    }

    @LogOperation(msg = "新增手动组卷试题集", type = OperationType.CREATE)
    @ApiOperation(value = "新增手动组卷试题集")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/manual/addlist", method = RequestMethod.POST)
    ResponseVo<List<ExamQuestionManualVo>> addList(@RequestBody ExamQuestionSetManualAddRequest addRequest) {
        return ResponseVo.success(questionManualService.addManualSet(addRequest));
    }

    @LogOperation(msg = "保存手动组卷试题", type = OperationType.UPDATE)
    @ApiOperation(value = "保存手动组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/manual/save", method = RequestMethod.POST)
    ResponseVo<ExamQuestionManualVo> save(@RequestBody ExamQuestionManualSaveRequest saveRequest) {
        return ResponseVo.success(questionManualService.saveManual(saveRequest));
    }

    @LogOperation(msg = "考试分析", type = OperationType.QUERY)
    @ApiOperation(value = "考试分析")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/analysis/{examId}", method = RequestMethod.GET)
    ResponseVo<ExamAnalysisVo> analysis(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.analysis(examId));
    }

    @LogOperation(msg = "删除手动组卷试题", type = OperationType.DELETE)
    @ApiOperation(value = "删除手动组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/manual/delete/{manualId}", method = RequestMethod.GET)
    ResponseVo<Boolean> deleteManual(@PathVariable("manualId") String manualId) {
        return ResponseVo.judge(questionManualService.deleteManual(manualId));
    }

    @LogOperation(msg = "新增智能组卷试题", type = OperationType.CREATE)
    @ApiOperation(value = "新增智能组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/intelligent/add", method = RequestMethod.POST)
    ResponseVo<ExamQuestionIntelligentVo> add(@RequestBody ExamQuestionIntelligentAddRequest addRequest) {
        return ResponseVo.success(questionIntelligentService.addIntelligent(addRequest));
    }

    @LogOperation(msg = "新增智能组卷试题集", type = OperationType.CREATE)
    @ApiOperation(value = "新增智能组卷试题集")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/intelligent/addlist", method = RequestMethod.POST)
    ResponseVo<List<ExamQuestionIntelligentVo>> addList(@RequestBody ExamQuestionSetIntelligentAddRequest addRequest) {
        return ResponseVo.success(questionIntelligentService.addIntelligentSet(addRequest));
    }

    @LogOperation(msg = "保存智能组卷试题", type = OperationType.UPDATE)
    @ApiOperation(value = "保存智能组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/intelligent/save", method = RequestMethod.POST)
    ResponseVo<ExamQuestionIntelligentVo> save(@RequestBody ExamQuestionIntelligentSaveRequest saveRequest) {
        return ResponseVo.success(questionIntelligentService.saveIntelligent(saveRequest));
    }

    @LogOperation(msg = "删除智能组卷试题", type = OperationType.DELETE)
    @ApiOperation(value = "删除智能组卷试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/intelligent/delete/{manualId}", method = RequestMethod.GET)
    ResponseVo<Boolean> deleteIntelligent(@PathVariable("intelligentId") String intelligentId) {
        return ResponseVo.judge(questionIntelligentService.deleteIntelligent(intelligentId));
    }

    @LogOperation(msg = "生成考试试卷", type = OperationType.CREATE)
    @ApiOperation(value = "生成考试试卷")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/paper/generate/{examId}", method = RequestMethod.GET)
    ResponseVo<Long> generatePaper(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.generatePaper(examId));
    }

    @LogOperation(msg = "复制考试试卷", type = OperationType.CREATE)
    @ApiOperation(value = "复制考试试卷")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/paper/duplicate/{examId}", method = RequestMethod.GET)
    ResponseVo<Long> duplicatePaper(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.duplicatePaper(examId));
    }

    @LogOperation(msg = "删除考试试卷", type = OperationType.DELETE)
    @ApiOperation(value = "删除考试试卷")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/paper/delete/{examId}", method = RequestMethod.GET)
    ResponseVo<Boolean> deletePaper(@PathVariable("examId") Long examId) {
        return ResponseVo.success(examService.deletePaper(examId));
    }

}
