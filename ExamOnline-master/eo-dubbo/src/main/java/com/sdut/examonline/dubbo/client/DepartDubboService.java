package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.dubbo.dto.DepartMembersDto;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/24 18:59
 */
public interface DepartDubboService {

    Map<String, Object> getAdminDashboard();

    List<Long> getUserDepartId(Long userId);

    List<Long> getMemberIds(Long departId);

    DepartMembersDto getMembers(Long departId);

    Map<String, DepartMembersDto> getMembersBatch(List<Long> departIds);

}
