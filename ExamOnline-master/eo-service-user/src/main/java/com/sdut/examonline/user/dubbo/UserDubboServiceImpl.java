package com.sdut.examonline.user.dubbo;

import com.google.common.collect.Lists;
import com.sdut.examonline.dubbo.client.UserDubboService;
import com.sdut.examonline.dubbo.dto.UserDto;
import com.sdut.examonline.user.service.UserService;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:59
 */
@Service(retries = 0)
public class UserDubboServiceImpl implements UserDubboService {

    @Autowired
    UserService userService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("user_count", userService.count());
        }};
    }

    @Override
    public String getInternalToken(String accessToken) {
        return userService.getInternalToken(accessToken);
    }

    @Override
    public UserDto getUserById(Long userId) {
        return MapperUtils.map(userService.getUserByIdInner(userId), UserDto.class);
    }

    @Override
    public List<UserDto> getUserByIdBatch(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Lists.newArrayList();
        }
        return MapperUtils.mapList(userService.getUserByIdBatch(userIds), UserDto.class);
    }
}
