package com.sdut.examonline.log.controller;

import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.log.service.AccessLogService;
import com.sdut.examonline.log.service.LoginLogService;
import com.sdut.examonline.log.service.OperationLogService;
import com.sdut.examonline.log.vo.AccessLogVo;
import com.sdut.examonline.log.vo.LoginLogVo;
import com.sdut.examonline.log.vo.OperationLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Api("日志模块")
@RestController
@RequestMapping("/api/log")
public class LogController {

    @Autowired
    AccessLogService accessLogService;

    @Autowired
    LoginLogService loginLogService;

    @Autowired
    OperationLogService operationLogService;

    @AccessLogIgnored
    @LogOperation(msg = "分页获取访问日志", type = OperationType.QUERY, ignoreResult = true)
    @ApiOperation(value = "分页获取访问日志")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/access", method = RequestMethod.POST)
    ResponseVo<PagingVo<AccessLogVo>> pageAccess(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(accessLogService.page(pagingDto));
    }

    @AccessLogIgnored
    @LogOperation(msg = "分页获取登录日志", type = OperationType.QUERY, ignoreResult = true)
    @ApiOperation(value = "分页获取登录日志")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    ResponseVo<PagingVo<LoginLogVo>> pageLogin(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(loginLogService.page(pagingDto));
    }

    @AccessLogIgnored
    @LogOperation(msg = "分页获取操作日志", type = OperationType.QUERY, ignoreResult = true)
    @ApiOperation(value = "分页获取操作日志")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/operation", method = RequestMethod.POST)
    ResponseVo<PagingVo<OperationLogVo>> pageOperation(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(operationLogService.page(pagingDto));
    }
}
