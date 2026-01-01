package com.sdut.examonline.log.mq.types;

import com.sdut.examonline.log.entity.OperationLog;
import com.sdut.examonline.log.mq.AbstractMessageQueueThread;
import com.sdut.examonline.log.service.OperationLogService;
import com.sdut.examonline.web.aspect.LogOperationAspect;
import com.sdut.examonline.web.dto.OperationLogDto;
import com.sdut.examonline.web.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/8 14:53
 */
@Component
public class OperationLogThread extends AbstractMessageQueueThread<OperationLogDto> {

    @Autowired
    OperationLogService operationLogService;

    @Override
    public String key() {
        return LogOperationAspect.KEY_MQ;
    }

    @Override
    public Class<OperationLogDto> type() {
        return OperationLogDto.class;
    }

    @Override
    public void process(OperationLogDto data) {
        operationLogService.save(MapperUtils.map(data, OperationLog.class));
    }
}
