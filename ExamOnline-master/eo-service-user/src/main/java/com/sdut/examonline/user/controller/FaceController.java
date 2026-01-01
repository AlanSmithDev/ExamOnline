package com.sdut.examonline.user.controller;

import com.sdut.examonline.common.constant.LoginLogType;
import com.sdut.examonline.common.constant.OperationType;
import com.sdut.examonline.web.annotation.LogLogin;
import com.sdut.examonline.user.service.FaceService;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.web.annotation.LogOperation;
import com.sdut.examonline.web.vo.ResponseVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api("用户管理")
@RestController
@RequestMapping("/api/user/face")
public class FaceController {

    @Autowired
    FaceService faceService;

    @ApiOperation(value = "检测图片并获取用户信息")
    @RequestMapping(value = "/checkFace/{code}", method = RequestMethod.POST)
    ResponseVo<Long> checkFace(@PathVariable("code") String code, @RequestParam("file") MultipartFile file) {
        return ResponseVo.success(faceService.checkFace(code, file));
    }

    @LogOperation(msg = "通过人脸识别确认二维码", type = OperationType.UPDATE)
    @ApiOperation(value = "通过人脸识别确认二维码")
    @RequestMapping(value = "/confirmQrCodeByFace/{code}", method = RequestMethod.POST)
    ResponseVo<Boolean> confirmQrCodeByFace(@PathVariable("code") String code, @RequestParam("file") MultipartFile file) {
        return ResponseVo.success(faceService.confirmQrCodeByFace(code, file));
    }

    @LogLogin(type = LoginLogType.LOG_IN)
    @ApiOperation(value = "通过人脸识别登录")
    @RequestMapping(value = "/loginByFace/{code}", method = RequestMethod.POST)
    ResponseVo<UserLoginVo> loginByFace(@PathVariable("code") String code, @RequestParam("file") MultipartFile file) {
        return ResponseVo.success(faceService.loginByFace(code, file));
    }

    @ApiOperation(value = "检测图片有且仅有一张人脸")
    @RequestMapping(value = "/detectFace", method = RequestMethod.POST)
    ResponseVo<Boolean> detectFace(@RequestParam("file") MultipartFile file) {
        return ResponseVo.success(faceService.detectFace(file));
    }

}
