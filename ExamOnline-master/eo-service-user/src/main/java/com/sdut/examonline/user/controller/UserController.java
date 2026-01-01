package com.sdut.examonline.user.controller;

import com.sdut.examonline.common.constant.LoginLogType;
import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.RoleConstants;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.web.annotation.AccessLogIgnored;
import com.sdut.examonline.web.annotation.LogLogin;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.user.dto.*;
import com.sdut.examonline.user.service.UserService;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.user.vo.UserVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.JwtUtils;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@Api("用户管理")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    UserService userService;

    @AccessLogIgnored
    @ApiOperation(value = "仪表盘数据")
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    ResponseVo<Map<String, Object>> dashboard() {
        return ResponseVo.success(userService.dashboard());
    }

    @AccessLogIgnored
    @LogLogin(type = LoginLogType.LOG_IN)
    @ApiOperation(value = "用户登录")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    ResponseVo<UserLoginVo> login(@RequestBody UserLoginRequest loginRequest) {
        return ResponseVo.success(userService.login(loginRequest));
    }

    @AccessLogIgnored
    @LogLogin(type = LoginLogType.LOG_IN)
    @ApiOperation(value = "用户注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseVo<UserLoginVo> register(@RequestBody UserRegisterRequest registerRequest) {
        return ResponseVo.success(userService.register(registerRequest));
    }

    @LogOperation(msg = "用户保存资料", type = OperationType.UPDATE)
    @ApiOperation(value = "用户保存资料")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    ResponseVo<UserVo> save(@RequestBody UserSaveRequest saveRequest) {
        return ResponseVo.success(userService.save(saveRequest));
    }

    @AccessLogIgnored
    @LogOperation(msg = "用户修改密码", type = OperationType.UPDATE)
    @ApiOperation(value = "用户修改密码")
    @RequestMapping(value = "/changePassword", method = RequestMethod.POST)
    ResponseVo<Boolean> changePassword(@RequestBody UserChangePasswordRequest saveRequest) {
        return ResponseVo.judge(userService.changePassword(saveRequest));
    }

    @LogOperation(msg = "上传头像", type = OperationType.UPDATE)
    @ApiOperation(value = "上传头像")
    @RequestMapping(value = "/uploadAvatar", method = RequestMethod.POST)
    ResponseVo<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        return ResponseVo.success(userService.uploadAvatar(file));
    }

    @LogOperation(msg = "上传人脸", type = OperationType.UPDATE)
    @ApiOperation(value = "上传人脸")
    @RequestMapping(value = "/uploadFace", method = RequestMethod.POST)
    ResponseVo<Boolean> uploadFace(@RequestParam("file") MultipartFile file) {
        return ResponseVo.judge(userService.uploadFace(file));
    }

    @LogOperation(msg = "获取当前用户信息", type = OperationType.QUERY)
    @ApiOperation(value = "获取当前用户信息")
    @RequestMapping(value = "/current", method = RequestMethod.GET)
    ResponseVo<UserVo> current() {
        return ResponseVo.success(userService.current());
    }

    @LogLogin(type = LoginLogType.LOG_OUT)
    @ApiOperation(value = "用户登出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    ResponseVo<Void> logout(HttpServletRequest request) {
        userService.logout(JwtUtils.getInternalToken(request));
        return ResponseVo.success();
    }

    @LogOperation(msg = "分页查询用户列表", type = OperationType.QUERY)
    @ApiOperation(value = "分页查询用户列表")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    ResponseVo<PagingVo<UserVo>> page(@RequestBody PagingDto pagingDto) {
        return ResponseVo.success(userService.page(pagingDto));
    }

    @AccessLogIgnored
    @LogOperation(msg = "从Excel导入用户", type = OperationType.IMPORT)
    @ApiOperation(value = "从Excel导入用户")
    @RequestMapping(value = "/import", method = RequestMethod.POST)
    ResponseVo<List<UserImportResult>> importFromExcel(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseVo.success(userService.importFromExcel(file.getInputStream()));
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

    @LogOperation(msg = "管理员修改用户密码", type = OperationType.UPDATE)
    @ApiOperation(value = "管理员修改用户密码")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/forceChangePassword", method = RequestMethod.POST)
    ResponseVo<Boolean> forceChangePassword(@RequestBody UserAdminChangePasswordRequest request) {
        return ResponseVo.success(userService.forceChangePassword(request));
    }

    @LogOperation(msg = "管理员修改用户身份", type = OperationType.UPDATE)
    @ApiOperation(value = "管理员修改用户身份")
    @PreAuthorize(RoleConstants.ANNOTATION_ADMIN_ONLY)
    @RequestMapping(value = "/forceChangeRole", method = RequestMethod.POST)
    ResponseVo<Boolean> forceChangeRole(@RequestBody UserAdminChangeRoleRequest request) {
        return ResponseVo.success(userService.forceChangeRole(request));
    }

}
