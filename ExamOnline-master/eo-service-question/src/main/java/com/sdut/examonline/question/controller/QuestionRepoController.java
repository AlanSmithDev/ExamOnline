package com.sdut.examonline.question.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.question.dto.QuestionRepoVerifyRequest;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.question.dto.QuestionRepoCreateRequest;
import com.sdut.examonline.question.dto.QuestionRepoSaveRequest;
import com.sdut.examonline.question.dto.QuestionRepoSearchFilter;
import com.sdut.examonline.question.service.QuestionRepoService;
import com.sdut.examonline.question.vo.QuestionRepoVo;
import com.sdut.examonline.question.vo.QuestionVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("题库管理")
@RestController
@PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
@RequestMapping("/api/question/repo")
public class QuestionRepoController {

    @Autowired
    QuestionRepoService questionRepoService;

    @LogOperation(msg = "获取用户创建的所有题库", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户创建的所有题库")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    ResponseVo<List<QuestionRepoVo>> list() {
        return ResponseVo.success(questionRepoService.all());
    }

    @LogOperation(msg = "获取所有共享题库", type = OperationType.QUERY)
    @ApiOperation(value = "获取所有共享题库")
    @RequestMapping(value = "/listPublic", method = RequestMethod.GET)
    ResponseVo<List<QuestionRepoVo>> listPublic() {
        return ResponseVo.success(questionRepoService.allPublic());
    }

    @LogOperation(msg = "分页获取用户创建的题库", type = OperationType.QUERY)
    @ApiOperation(value = "分页获取用户创建的题库")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    ResponseVo<PagingVo<QuestionRepoVo>> page(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(questionRepoService.page(pagingDto));
    }

    @LogOperation(msg = "分页获取共享题库", type = OperationType.QUERY)
    @ApiOperation(value = "分页获取共享题库")
    @RequestMapping(value = "/pagePublic", method = RequestMethod.POST)
    ResponseVo<PagingVo<QuestionRepoVo>> pagePublic(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(questionRepoService.pagePublic(pagingDto));
    }

    @LogOperation(msg = "分页查询题库的所有题目", type = OperationType.QUERY)
    @ApiOperation(value = "分页查询题库的所有题目")
    @RequestMapping(value = "/questions", method = RequestMethod.POST)
    ResponseVo<PagingVo<QuestionVo>> questions(@RequestBody PagingParamDto<Long> pagingParamDto) {
        return ResponseVo.success(questionRepoService.questions(pagingParamDto));
    }

    @LogOperation(msg = "查询题库的所有题目", type = OperationType.QUERY)
    @ApiOperation(value = "查询题库的所有题目")
    @RequestMapping(value = "/questions/{repoId}", method = RequestMethod.POST)
    ResponseVo<List<QuestionVo>> questions(@PathVariable("repoId") Long repoId) {
        return ResponseVo.success(questionRepoService.questions(repoId));
    }

    @LogOperation(msg = "新建题库", type = OperationType.CREATE)
    @ApiOperation(value = "新建题库")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    ResponseVo<QuestionRepoVo> create(@RequestBody QuestionRepoCreateRequest createRequest) {
        return ResponseVo.success(questionRepoService.create(createRequest));
    }

    @LogOperation(msg = "保存题库", type = OperationType.UPDATE)
    @ApiOperation(value = "保存题库")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    ResponseVo<QuestionRepoVo> save(@RequestBody QuestionRepoSaveRequest saveRequest) {
        return ResponseVo.success(questionRepoService.save(saveRequest));
    }

    @LogOperation(msg = "验证题库密码", type = OperationType.OTHER)
    @ApiOperation(value = "验证题库密码")
    @RequestMapping(value = "/verify", method = RequestMethod.POST)
    ResponseVo<Boolean> verify(@RequestBody QuestionRepoVerifyRequest request) {
        return ResponseVo.success(questionRepoService.verify(request));
    }

    @LogOperation(msg = "复制题目至题库", type = OperationType.CREATE)
    @ApiOperation(value = "复制题目至题库")
    @RequestMapping(value = "/duplicate", method = RequestMethod.GET)
    ResponseVo<QuestionVo> duplicate(@ApiParam(value = "题目ID", required = true) @RequestParam("questionId") Long questionId,
                                     @ApiParam(value = "目标题库ID", required = true) @RequestParam("destRepoId") Long destRepoId) {
        return ResponseVo.success(questionRepoService.duplicateFromRepo(questionId, destRepoId));
    }

    @LogOperation(msg = "分页搜索题库", type = OperationType.QUERY)
    @ApiOperation(value = "分页搜索题库")
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseVo<PagingVo<QuestionRepoVo>> search(@RequestBody PagingParamDto<QuestionRepoSearchFilter> paramDto) {
        return ResponseVo.success(questionRepoService.search(paramDto));
    }

    @LogOperation(msg = "删除题库", type = OperationType.DELETE)
    @ApiOperation(value = "删除题库")
    @RequestMapping(value = "/delete/{repoId}", method = RequestMethod.GET)
    ResponseVo<Boolean> delete(@PathVariable("repoId") Long repoId) {
        return ResponseVo.judge(questionRepoService.delete(repoId));
    }

}
