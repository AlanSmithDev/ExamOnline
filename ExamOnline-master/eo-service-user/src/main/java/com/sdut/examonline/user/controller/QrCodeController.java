package com.sdut.examonline.user.controller;

import com.sdut.examonline.common.constant.LoginLogType;
import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.common.constant.QrCodeState;
import com.sdut.examonline.user.service.QrCodeService;
import com.sdut.examonline.user.vo.QrCodeVo;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.web.annotation.LogLogin;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("用户管理")
@RestController
@RequestMapping("/api/user/qrcode")
public class QrCodeController {

    @Autowired
    QrCodeService qrCodeService;

    @ApiOperation(value = "刷新二维码")
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    ResponseVo<QrCodeVo> createQrCode(@RequestParam("type") String type, @RequestParam(value = "option", defaultValue = "") String option) {
        return ResponseVo.success(qrCodeService.createQrCode(type, option));
    }

    @ApiOperation(value = "获取二维码状态")
    @RequestMapping(value = "/check/{code}", method = RequestMethod.GET)
    ResponseVo<String> checkState(@PathVariable("code") String code) {
        return ResponseVo.success(qrCodeService.checkState(code));
    }

    @LogOperation(msg = "扫描二维码", type = OperationType.UPDATE)
    @ApiOperation(value = "扫描二维码")
    @RequestMapping(value = "/scan/{code}", method = RequestMethod.GET)
    ResponseVo<Void> onScan(@PathVariable("code") String code) {
        qrCodeService.updateState(code, QrCodeState.SCANNED);
        return ResponseVo.success();
    }

    @LogOperation(msg = "确认登录", type = OperationType.UPDATE)
    @ApiOperation(value = "确认登录")
    @RequestMapping(value = "/confirm/{code}", method = RequestMethod.GET)
    ResponseVo<Boolean> confirm(@PathVariable("code") String code) {
        return ResponseVo.success(qrCodeService.confirm(code));
    }

    @LogLogin(type = LoginLogType.LOG_IN)
    @ApiOperation(value = "通过扫码登录")
    @RequestMapping(value = "/login/{code}", method = RequestMethod.GET)
    ResponseVo<UserLoginVo> loginByCode(@PathVariable("code") String code) {
        return ResponseVo.success(qrCodeService.loginByCode(code));
    }

}
