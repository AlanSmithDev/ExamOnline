package com.sdut.examonline.log.dubbo;

import com.sdut.examonline.dubbo.client.LogDubboService;
import com.sdut.examonline.log.service.AccessLogService;
import com.sdut.examonline.log.service.LoginLogService;
import com.sdut.examonline.log.service.OperationLogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/4/9 22:21
 */
@Service(retries = 0)
public class LogDubboServiceImpl implements LogDubboService {

    @Autowired
    AccessLogService accessLogService;

    @Autowired
    LoginLogService loginLogService;

    @Autowired
    OperationLogService operationLogService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("access_log_count", accessLogService.count());
            put("login_log_count", loginLogService.count());
            put("operation_log_count", operationLogService.count());
        }};
    }

}
