package com.sdut.examonline.user.service;

import com.alibaba.fastjson.JSONObject;
import com.sdut.examonline.user.vo.QrCodeVo;
import com.sdut.examonline.user.vo.UserLoginVo;

/**
 * @author Chen
 * @since 2022/3/23 21:57
 */
public interface QrCodeService {

    /**
     * 刷新二维码
     *
     * @param type 二维码类型
     * @param option 额外参数
     */
    QrCodeVo createQrCode(String type, String option);

    /**
     * 获取二维码状态
     * 0为未扫描
     * 1为检测失败
     * 2为检测成功
     * 前端轮询此接口
     * 返回2时,调用loginByCode以获得token
     */
    String checkState(String code);

    /**
     * 获取二维码内容
     *
     * @param code 二维码id
     */
    JSONObject getQrCodeBody(String code);

    /**
     * 更新二维码状态
     * */
    void updateState(String code, String state);

    /**
     * 确认登录
     */
    Boolean confirm(String code);

    /**
     * 通过扫码登录
     */
    UserLoginVo loginByCode(String code);

    /**
     * 删除二维码
     */
    void deleteQrCode(String code);

}
