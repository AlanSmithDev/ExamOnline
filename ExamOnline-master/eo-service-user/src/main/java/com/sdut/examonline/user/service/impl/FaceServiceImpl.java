package com.sdut.examonline.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.QrCodeState;
import com.sdut.examonline.common.constant.QrCodeType;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.constant.UserPlatform;
import com.sdut.examonline.user.entity.User;
import com.sdut.examonline.user.service.FaceService;
import com.sdut.examonline.user.service.QrCodeService;
import com.sdut.examonline.user.service.UserService;
import com.sdut.examonline.user.util.BaiduFaceUtils;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * @author Chen
 * @since 2022/3/17 19:42
 */
@Slf4j
@Service
public class FaceServiceImpl implements FaceService {

    @Autowired
    CacheUtils cacheUtils;

    @Autowired
    UserService userService;

    @Autowired
    QrCodeService qrCodeService;

    @Override
    public Long checkFace(String code, MultipartFile file) {
        try {
            String userIdStr = BaiduFaceUtils.checkFace(Base64.getEncoder().encodeToString(file.getBytes()));
            if (userIdStr == null) {
                qrCodeService.updateState(code, QrCodeState.SCANNED);
                return null;
            } else {
                Long userId = Long.parseLong(userIdStr);
                qrCodeService.updateState(code, QrCodeState.CONFIRMED);
                return userId;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
    }

    @Override
    public Boolean confirmQrCodeByFace(String code, MultipartFile file) {
        Long userId = checkFace(code, file);
        if (userId == null || userId <= 0) {
            return false;
        }
        User user = userService.getUserByIdInner(userId);
        if (user == null) {
            return false;
        }
        JSONObject body = qrCodeService.getQrCodeBody(code);
        qrCodeService.updateState(code, String.valueOf(user.getId()));
        if (QrCodeType.PAPER_CHECK_FACE.equals(body.getString("type"))) {
            cacheUtils.setString("paper:cf:" + body.getString("option"), "checked", 30, TimeUnit.MINUTES);
        }
        return true;
    }

    @Override
    public UserLoginVo loginByFace(String code, MultipartFile file) {
        Long userId = checkFace(code, file);
        if (userId == null || userId <= 0) {
            return null;
        }
        User user = userService.getUserByIdInner(userId);
        if (user == null) {
            return null;
        }
        qrCodeService.deleteQrCode(code);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUserName(), String.valueOf(user.getId()), Lists.newArrayList(new SimpleGrantedAuthority(user.getRole())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserLoginVo loginVo = new UserLoginVo();
        MapperUtils.map(user, loginVo);
        loginVo.setToken(userService.generateToken(user, UserPlatform.APP).getA());
        return loginVo;
    }

    @Override
    public Boolean detectFace(MultipartFile file) {
        try {
            return BaiduFaceUtils.detectFace(Base64.getEncoder().encodeToString(file.getBytes()));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
    }

}
