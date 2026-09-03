package com.sdut.examonline.log.mq.types;

import com.sdut.examonline.log.entity.AccessLog;
import com.sdut.examonline.log.mq.AbstractMessageQueueThread;
import com.sdut.examonline.log.service.AccessLogService;
import com.sdut.examonline.web.aspect.LogAccessAspect;
import com.sdut.examonline.web.dto.AccessLogDto;
import com.sdut.examonline.web.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/8 14:53
 */
@Component
public class AccessLogThread extends AbstractMessageQueueThread<AccessLogDto> {

    @Autowired
    AccessLogService accessLogService;

    @Override
    public String key() {
        return LogAccessAspect.KEY_MQ;
    }

    @Override
    public Class<AccessLogDto> type() {
        return AccessLogDto.class;
    }

    @Override
    public void process(AccessLogDto data) {
        accessLogService.save(MapperUtils.map(data, AccessLog.class));
    }
}
