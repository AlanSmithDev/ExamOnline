package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.dubbo.dto.UserDto;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:26
 */
public interface UserDubboService {

    Map<String, Object> getAdminDashboard();

    String getInternalToken(String accessToken);

    UserDto getUserById(Long userId);

    List<UserDto> getUserByIdBatch(List<Long> userIds);

}
