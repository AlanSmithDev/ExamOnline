package com.sdut.examonline.log.mq.types;

import com.sdut.examonline.log.entity.LoginLog;
import com.sdut.examonline.log.mq.AbstractMessageQueueThread;
import com.sdut.examonline.log.service.LoginLogService;
import com.sdut.examonline.web.aspect.LogLoginAspect;
import com.sdut.examonline.web.dto.LoginLogDto;
import com.sdut.examonline.web.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/8 14:53
 */
@Component
public class LoginLogThread extends AbstractMessageQueueThread<LoginLogDto> {

    @Autowired
    LoginLogService loginLogService;

    @Override
    public String key() {
        return LogLoginAspect.KEY_MQ;
    }

    @Override
    public Class<LoginLogDto> type() {
        return LoginLogDto.class;
    }

    @Override
    public void process(LoginLogDto data) {
        loginLogService.save(MapperUtils.map(data, LoginLog.class));
    }
}
