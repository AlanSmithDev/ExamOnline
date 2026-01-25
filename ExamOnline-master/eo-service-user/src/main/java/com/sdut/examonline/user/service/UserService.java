package com.sdut.examonline.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.common.util.Tuple;
import com.sdut.examonline.user.dto.*;
import com.sdut.examonline.user.entity.User;
import com.sdut.examonline.user.vo.UserLoginVo;
import com.sdut.examonline.user.vo.UserVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface UserService extends IService<User> {

    /**
     * 仪表盘数据
     *
     */
    Map<String, Object> dashboard();

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     */
    UserLoginVo login(UserLoginRequest loginRequest);

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     */
    UserLoginVo register(UserRegisterRequest registerRequest);

    /**
     * 用户保存资料
     *
     * @param saveRequest 保存请求
     */
    UserVo save(UserSaveRequest saveRequest);

    /**
     * 用户修改密码
     *
     * @param changePasswordRequest 修改密码请求
     */
    Boolean changePassword(UserChangePasswordRequest changePasswordRequest);

    /**
     * 上传头像
     *
     * @param file 头像
     */
    String uploadAvatar(MultipartFile file);

    /**
     * 上传人脸
     *
     * @param file 人脸图片
     */
    Boolean uploadFace(MultipartFile file);

    /**
     * 获取当前用户信息
     */
    UserVo current();

    /**
     * 退出登录
     *
     * @param token 令牌
     */
    void logout(String token);

    /**
     * 以外部暴露令牌换取真实令牌
     *
     * @param accessToken 外部令牌
     */
    String getInternalToken(String accessToken);

    /**
     * 分页查询用户列表
     *
     * @param pagingDto 分页请求
     */
    PagingVo<UserVo> page(PagingDto pagingDto);

    /**
     * 获取用户信息(内部调用)
     *
     * @param userId 用户ID
     */
    User getUserByIdInner(Long userId);

    /**
     * 批量获取用户信息(内部调用)
     */
    List<User> getUserByIdBatch(List<Long> userIds);

    /**
     * 从Excel导入用户
     */
    List<UserImportResult> importFromExcel(InputStream inputStream);

    /**
     * 管理员修改用户密码
     *
     * @param request 请求
     */
    Boolean forceChangePassword(UserAdminChangePasswordRequest request);

    /**
     * 管理员修改用户身份
     *
     * @param request 请求
     */
    Boolean forceChangeRole(UserAdminChangeRoleRequest request);

    /**
     * 生成令牌(内部调用)
     *
     * @param user 用户对象
     * @param platform 平台
     * */
    Tuple<String, String> generateToken(User user, Integer platform);

}
