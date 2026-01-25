package com.sdut.examonline.paper.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.paper.dto.PaperAnswerCorrectRequest;
import com.sdut.examonline.paper.dto.PaperAnswerSaveRequest;
import com.sdut.examonline.paper.dto.PaperRecordExportDto;
import com.sdut.examonline.paper.service.PaperAnswerService;
import com.sdut.examonline.paper.service.PaperRecordService;
import com.sdut.examonline.paper.service.PaperService;
import com.sdut.examonline.paper.vo.*;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.util.ExcelUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.PoitlUtils;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Api("试卷管理")
@RestController
@RequestMapping("/api/paper")
public class PaperController {

    @Autowired
    PaperService paperService;

    @Autowired
    PaperAnswerService paperAnswerService;

    @Autowired
    PaperRecordService paperRecordService;

    @LogOperation(msg = "获取试卷详情", type = OperationType.QUERY)
    @ApiOperation(value = "获取试卷详情")
    @RequestMapping(value = "/detail/{paperId}", method = RequestMethod.GET)
    ResponseVo<PaperDetailVo> detail(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperService.detail(paperId));
    }

    @LogOperation(msg = "保存答题记录", type = OperationType.UPDATE)
    @ApiOperation(value = "保存答题记录")
    @RequestMapping(value = "/answer/save", method = RequestMethod.POST)
    ResponseVo<PaperAnswerVo> save(@RequestBody PaperAnswerSaveRequest saveRequest) {
        return ResponseVo.success(paperAnswerService.save(saveRequest));
    }

    @LogOperation(msg = "获取自己的考试答题记录", type = OperationType.QUERY)
    @ApiOperation(value = "获取自己的考试答题记录")
    @RequestMapping(value = "/answers/my/{paperId}", method = RequestMethod.GET)
    ResponseVo<List<PaperAnswerVo>> myAnswers(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperAnswerService.myAnswers(paperId));
    }

    @LogOperation(msg = "获取学生的考试答题记录", type = OperationType.QUERY)
    @ApiOperation(value = "获取学生的考试答题记录")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/answers/{paperId}/{userId}", method = RequestMethod.GET)
    ResponseVo<List<PaperAnswerVo>> answers(@PathVariable("paperId") Long paperId, @PathVariable("userId") Long userId) {
        return ResponseVo.success(paperAnswerService.answers(paperId, userId));
    }

    @LogOperation(msg = "开始答题", type = OperationType.CREATE)
    @ApiOperation(value = "开始答题")
    @RequestMapping(value = "/start/{paperId}", method = RequestMethod.GET)
    ResponseVo<PaperRecordVo> startAnswer(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperService.startAnswer(paperId, JwtUtils.getUserId()));
    }

    @LogOperation(msg = "提交试卷", type = OperationType.UPDATE)
    @ApiOperation(value = "提交试卷")
    @RequestMapping(value = "/submit/{paperId}", method = RequestMethod.GET)
    ResponseVo<PaperRecordVo> submit(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperService.submit(paperId));
    }

    @LogOperation(msg = "批改试题", type = OperationType.UPDATE)
    @ApiOperation(value = "批改试题")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/correct", method = RequestMethod.POST)
    ResponseVo<PaperAnswerVo> correct(@RequestBody PaperAnswerCorrectRequest request) {
        return ResponseVo.success(paperAnswerService.correct(request));
    }

    @LogOperation(msg = "获取下一个需要批改的答题记录", type = OperationType.QUERY)
    @ApiOperation(value = "获取下一个需要批改的答题记录")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/nextAnswer/{paperId}/{questionId}", method = RequestMethod.GET)
    ResponseVo<PaperAnswerVo> nextAnswer(@PathVariable("paperId") Long paperId, @PathVariable("questionId") Long questionId) {
        return ResponseVo.success(paperAnswerService.nextAnswer(paperId, questionId));
    }

    @LogOperation(msg = "获取试卷的批改详情", type = OperationType.QUERY)
    @ApiOperation(value = "获取试卷的批改详情")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/correct/{paperId}/info", method = RequestMethod.GET)
    ResponseVo<PaperCorrectInfoVo> getCorrectInfo(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperAnswerService.getCorrectInfo(paperId));
    }

    @ApiOperation(value = "批改试卷(仅测试使用)")
    @RequestMapping(value = "/correct/{paperId}", method = RequestMethod.GET)
    ResponseVo<Void> correct(@PathVariable("paperId") Long paperId) {
        paperService.correct(paperId, JwtUtils.getUserId());
        return ResponseVo.success();
    }

    @LogOperation(msg = "分页查询试卷的成绩记录列表", type = OperationType.QUERY)
    @ApiOperation(value = "分页查询试卷的成绩记录列表")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/records/page", method = RequestMethod.POST)
    ResponseVo<PagingVo<PaperRecordVo>> records(@RequestBody PagingParamDto<Long> paramDto) {
        return ResponseVo.success(paperRecordService.pageByPaperId(paramDto));
    }

    @LogOperation(msg = "获取试卷的所有成绩记录", type = OperationType.QUERY)
    @ApiOperation(value = "获取试卷的所有成绩记录")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/records/list/{paperId}", method = RequestMethod.GET)
    ResponseVo<List<PaperRecordVo>> records(@PathVariable("paperId") Long paperId) {
        return ResponseVo.success(paperRecordService.listByPaperId(paperId));
    }

    @LogOperation(msg = "分页查询已批改完毕的成绩记录列表", type = OperationType.QUERY)
    @ApiOperation(value = "分页查询已批改完毕的成绩记录列表")
    @RequestMapping(value = "/records/my/page/corrected", method = RequestMethod.POST)
    ResponseVo<PagingVo<PaperRecordDetailVo>> pageCorrected(@RequestBody PagingParamDto<Long> paramDto) {
        return ResponseVo.success(paperRecordService.pageCorrected(paramDto));
    }

    @LogOperation(msg = "分页查询用户的成绩记录列表", type = OperationType.QUERY)
    @ApiOperation(value = "分页查询用户的成绩记录列表")
    @RequestMapping(value = "/records/my/page", method = RequestMethod.POST)
    ResponseVo<PagingVo<PaperRecordVo>> recordsUser(@RequestBody PagingParamDto<Long> paramDto) {
        return ResponseVo.success(paperRecordService.pageByUserId(paramDto));
    }

    @LogOperation(msg = "获取用户的所有成绩记录", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户的所有成绩记录")
    @RequestMapping(value = "/records/my/list", method = RequestMethod.GET)
    ResponseVo<List<PaperRecordVo>> recordsUser() {
        return ResponseVo.success(paperRecordService.listByUserId(JwtUtils.getUserId()));
    }

    @AccessLogIgnored
    @LogOperation(msg = "导出试卷成绩", type = OperationType.EXPORT)
    @ApiOperation(value = "导出试卷成绩")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/records/export/{paperId}", method = RequestMethod.GET)
    void exportRecords(@PathVariable("paperId") Long paperId, HttpServletResponse response) throws IOException {
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=records.xls");
        exportRecords2Stream(response.getOutputStream(), paperId);
    }

    @AccessLogIgnored
    @LogOperation(msg = "导出试卷", type = OperationType.EXPORT)
    @ApiOperation(value = "导出试卷")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/export/{paperId}", method = RequestMethod.GET)
    void exportPaper(@PathVariable("paperId") Long paperId, HttpServletResponse response) throws Exception {
        String fileName = "template.docx";
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName);
        exportPaper2Stream(response.getOutputStream(), paperId);
    }

    @AccessLogIgnored
    @LogOperation(msg = "考试附件打包导出", type = OperationType.EXPORT)
    @ApiOperation(value = "考试附件打包导出")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @RequestMapping(value = "/export/{paperId}/zip", method = RequestMethod.GET)
    void exportZip(@PathVariable("paperId") Long paperId, HttpServletResponse response) throws Exception {
        ZipOutputStream zos = new ZipOutputStream(response.getOutputStream());

        //导出成绩xls到zip
        zos.putNextEntry(new ZipEntry("成绩统计.xls"));
        exportRecords2Stream(zos, paperId);
        zos.closeEntry();

        //导出试卷docx到zip
        zos.putNextEntry(new ZipEntry("试卷.docx"));
        exportPaper2Stream(zos, paperId);
        zos.closeEntry();

        zos.flush();
        zos.close();

        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=package.zip");
    }

    private void exportRecords2Stream(OutputStream outputStream, Long paperId) {
        List<PaperRecordExportDto> records = paperRecordService.getExportPaperRecords(paperId);
        ExcelUtils.writeExcel(outputStream, records, PaperRecordExportDto.class);
    }

    private void exportPaper2Stream(OutputStream outputStream, Long paperId) throws IOException {
        String fileName = "template.docx";
        ClassPathResource resource = new ClassPathResource(fileName);
        if (!resource.exists()) {
            log.info("导入模板文件(template.docx)不存在");
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
        Map<String, Object> options = paperService.exportPaper(paperId);
        PoitlUtils.writeWord(resource.getInputStream(), outputStream, options);
    }

}
