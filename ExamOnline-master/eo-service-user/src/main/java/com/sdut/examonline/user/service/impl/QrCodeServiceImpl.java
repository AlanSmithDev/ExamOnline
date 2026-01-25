package com.sdut.examonline.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.sdut.examonline.common.constant.QrCodeState;
import com.sdut.examonline.common.exception.ErrorCode;
import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.common.constant.UserPlatform;
import com.sdut.examonline.user.entity.User;
import com.sdut.examonline.user.service.QrCodeService;
import com.sdut.examonline.user.service.UserService;
import com.sdut.examonline.user.util.QrCodeUtils;
import com.sdut.examonline.user.vo.QrCodeVo;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.web.util.CacheUtils;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author Chen
 * @since 2022/3/23 22:00
 */
@Slf4j
@Service
public class QrCodeServiceImpl implements QrCodeService {

    @Autowired
    UserService userService;

    @Autowired
    CacheUtils cacheUtils;

    @Override
    public QrCodeVo createQrCode(String type, String option) {
        try {
            QrCodeVo qrCodeVo = new QrCodeVo();
            String code = RandomStringUtils.randomAlphabetic(12);
            qrCodeVo.setCode(code);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("code", code);
            jsonObject.put("type", type);
            jsonObject.put("option", option);
            qrCodeVo.setImage(QrCodeUtils.createQrCodeBase64(jsonObject.toJSONString()));
            cacheUtils.setString(getCacheKey(code), QrCodeState.WAITING_SCAN, 5, TimeUnit.MINUTES);
            cacheUtils.setString(getCacheKeyBody(code), jsonObject.toJSONString(), 5, TimeUnit.MINUTES);
            return qrCodeVo;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new ServiceException(ErrorCode.INNER_ERROR);
        }
    }

    @Override
    public String checkState(String code) {
        String state = cacheUtils.getString(getCacheKey(code));
        if (cacheUtils.isNullString(state) || StringUtils.isEmpty(state)) {
            return QrCodeState.EXPIRED;
        }
        if (!QrCodeState.WAITING_SCAN.equals(state) && !QrCodeState.SCANNED.equals(state)) {
            return QrCodeState.CONFIRMED;
        }
        return state;
    }

    @Override
    public JSONObject getQrCodeBody(String code) {
        return JSON.parseObject(cacheUtils.getString(getCacheKeyBody(code)));
    }

    @Override
    public void updateState(String code, String state) {
        cacheUtils.setStringIfPresent(getCacheKey(code), state, 5, TimeUnit.MINUTES);
    }

    @Override
    public Boolean confirm(String code) {
        if (!UserPlatform.APP.equals(JwtUtils.getPlatform())) {
            return false;
        }
        Long userId = JwtUtils.getUserId();
        User user = userService.getUserByIdInner(userId);
        if (user == null) {
            return null;
        }
        updateState(code, String.valueOf(user.getId()));
        return true;
    }

    @Override
    public UserLoginVo loginByCode(String code) {
        String state = cacheUtils.getString(getCacheKey(code));
        if (cacheUtils.isNullString(state) || StringUtils.isEmpty(state) || state.length() <= 1) {
            return null;
        }
        long userId;
        try {
            userId = Long.parseLong(state);
        } catch (Exception e) {
            throw new ServiceException(400, "请确认登录");
        }
        User user = userService.getUserByIdInner(userId);
        if (user == null) {
            return null;
        }
        deleteQrCode(code);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getUserName(), String.valueOf(user.getId()), Lists.newArrayList(new SimpleGrantedAuthority(user.getRole())));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserLoginVo loginVo = new UserLoginVo();
        MapperUtils.map(user, loginVo);
        loginVo.setToken(userService.generateToken(user, UserPlatform.H5).getA());
        return loginVo;
    }

    @Override
    public void deleteQrCode(String code) {
        cacheUtils.delete(getCacheKey(code));
        cacheUtils.delete(getCacheKeyBody(code));
    }

    private String getCacheKey(String code) {
        return "qrcode:" + code;
    }

    private String getCacheKeyBody(String code) {
        return "qrcode:" + code + ":body";
    }
}
