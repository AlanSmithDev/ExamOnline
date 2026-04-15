package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.dubbo.dto.*;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:26
 */
public interface PaperDubboService {

    Map<String, Object> getAdminDashboard();

    Long joinManual(PaperSettingDto paperSetting, List<QuestionManualDto> settings) throws ServiceException;

    Long joinIntelligent(PaperSettingDto paperSetting, List<QuestionIntelligentDto> settings) throws ServiceException;

    PaperDetailDto detail(Long paperId) throws ServiceException;

    Long duplicate(Long paperId) throws ServiceException;

    Boolean delete(Long paperId) throws ServiceException;

    PaperRecordDto getPaperRecord(Long paperId, Long userId);

    List<PaperRecordDto> getPaperRecords(Long paperId);

}
