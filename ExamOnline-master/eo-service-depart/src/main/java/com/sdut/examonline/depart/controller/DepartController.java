package com.sdut.examonline.depart.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.depart.dto.*;
import com.sdut.examonline.depart.service.DepartService;
import com.sdut.examonline.depart.vo.DepartDetailVo;
import com.sdut.examonline.depart.vo.DepartMemberVo;
import com.sdut.examonline.depart.vo.DepartVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api("班级管理")
@RestController
@RequestMapping("/api/depart")
public class DepartController {

    @Autowired
    DepartService departService;

    @LogOperation(msg = "添加班级", type = OperationType.CREATE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "添加班级")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    ResponseVo<DepartVo> add(@RequestBody DepartCreateRequest createRequest) {
        return ResponseVo.success(departService.add(createRequest));
    }

    @LogOperation(msg = "保存班级", type = OperationType.UPDATE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "保存班级")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    ResponseVo<DepartVo> save(@RequestBody DepartSaveRequest saveRequest) {
        return ResponseVo.success(departService.save(saveRequest));
    }

    @LogOperation(msg = "删除班级", type = OperationType.DELETE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "删除班级")
    @RequestMapping(value = "/delete/{departId}", method = RequestMethod.GET)
    ResponseVo<Boolean> delete(@PathVariable("departId") Long departId) {
        return ResponseVo.success(departService.delete(departId));
    }

    @LogOperation(msg = "获取班级信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取班级信息")
    @RequestMapping(value = "/getDepart/{departId}", method = RequestMethod.GET)
    ResponseVo<DepartVo> getDepart(@PathVariable("departId") Long departId) {
        return ResponseVo.success(departService.getDepart(departId));
    }

    @LogOperation(msg = "加入班级", type = OperationType.CREATE)
    @ApiOperation(value = "加入班级")
    @RequestMapping(value = "/join/{code}", method = RequestMethod.GET)
    ResponseVo<DepartVo> join(@PathVariable("code") String code) {
        return ResponseVo.success(departService.join(code));
    }

    @LogOperation(msg = "获取用户班级信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取用户班级信息")
    @RequestMapping(value = "/myDepart", method = RequestMethod.GET)
    ResponseVo<List<DepartVo>> myDepart() {
        return ResponseVo.success(departService.myDepart());
    }

    @LogOperation(msg = "分页获取班级列表", type = OperationType.QUERY)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "分页获取班级列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    ResponseVo<PagingVo<DepartVo>> page(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(departService.page(pagingDto));
    }

    @LogOperation(msg = "列出所有班级", type = OperationType.QUERY)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "列出所有班级")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    ResponseVo<List<DepartVo>> all() {
        return ResponseVo.success(departService.all());
    }

    @LogOperation(msg = "获取班级及成员信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取班级及成员信息")
    @RequestMapping(value = "/detail/{departId}", method = RequestMethod.GET)
    ResponseVo<DepartDetailVo> detail(@PathVariable("departId") Long departId) {
        return ResponseVo.success(departService.detail(departId));
    }

    @LogOperation(msg = "添加班级成员", type = OperationType.CREATE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "添加班级成员")
    @RequestMapping(value = "/member/add", method = RequestMethod.POST)
    ResponseVo<DepartMemberVo> add(@RequestBody DepartMemberAddRequest request) {
        return ResponseVo.success(departService.add(request));
    }

    @LogOperation(msg = "批量添加班级成员", type = OperationType.CREATE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "批量添加班级成员")
    @RequestMapping(value = "/member/addBatch", method = RequestMethod.POST)
    ResponseVo<List<DepartMemberVo>> addBatch(@RequestBody DepartMemberAddBatchRequest request) {
        return ResponseVo.success(departService.addBatch(request));
    }

    @LogOperation(msg = "删除班级成员", type = OperationType.DELETE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "删除班级成员")
    @RequestMapping(value = "/member/remove", method = RequestMethod.POST)
    ResponseVo<Boolean> remove(@RequestBody DepartMemberRemoveRequest request) {
        return ResponseVo.success(departService.remove(request));
    }

    @LogOperation(msg = "批量删除班级成员", type = OperationType.DELETE)
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_OR_TEACHER)
    @ApiOperation(value = "批量删除班级成员")
    @RequestMapping(value = "/member/removeBatch", method = RequestMethod.POST)
    ResponseVo<Boolean> removeBatch(@RequestBody DepartMemberRemoveBatchRequest request) {
        return ResponseVo.success(departService.removeBatch(request));
    }

}
