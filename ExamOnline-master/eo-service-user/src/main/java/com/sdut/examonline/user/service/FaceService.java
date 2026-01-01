package com.sdut.examonline.user.service;

import com.sdut.examonline.user.vo.UserLoginVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Chen
 * @since 2022/3/17 18:27
 */
public interface FaceService {

    /**
     * 检测图片并获取用户信息
     */
    Long checkFace(String code, MultipartFile file);

    /**
     * 通过人脸识别确认二维码
     */
    Boolean confirmQrCodeByFace(String code, MultipartFile file);

    /**
     * 通过人脸识别登录
     */
    UserLoginVo loginByFace(String code, MultipartFile file);

    /**
     * 检测图片有且仅有一张人脸
     */
    Boolean detectFace(MultipartFile file);

}
