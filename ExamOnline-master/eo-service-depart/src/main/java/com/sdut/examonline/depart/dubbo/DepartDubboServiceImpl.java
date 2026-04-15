package com.sdut.examonline.depart.dubbo;

import com.google.common.collect.Lists;
import com.sdut.examonline.depart.service.DepartService;
import com.sdut.examonline.depart.vo.DepartVo;
import com.sdut.examonline.dubbo.client.DepartDubboService;
import com.sdut.examonline.dubbo.dto.DepartMembersDto;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/24 19:01
 */
@Service(retries = 0)
public class DepartDubboServiceImpl implements DepartDubboService {

    @Autowired
    DepartService departService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("depart_count", departService.count());
        }};
    }

    @Override
    public List<Long> getUserDepartId(Long userId) {
        List<DepartVo> departs = departService.getDepartsByUserId(userId);
        List<Long> idList = Lists.newArrayList();
        for (DepartVo depart : departs) {
            idList.add(depart.getId());
        }
        return idList;
    }

    @Override
    public List<Long> getMemberIds(Long departId) {
        return departService.getDepartMemberIdsInner(departId);
    }

    @Override
    public DepartMembersDto getMembers(Long departId) {
        return departService.getMembersDubbo(departId);
    }

    @Override
    public Map<String, DepartMembersDto> getMembersBatch(List<Long> departIds) {
        return departService.getMembersBatchDubbo(departIds);
    }

}
