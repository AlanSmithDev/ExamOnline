package com.sdut.examonline.paper.dubbo;

import com.sdut.examonline.common.exception.ServiceException;
import com.sdut.examonline.dubbo.client.PaperDubboService;
import com.sdut.examonline.dubbo.dto.*;
import com.sdut.examonline.paper.service.PaperRecordService;
import com.sdut.examonline.paper.service.PaperService;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/19 8:45
 */
@Service(retries = 0)
public class PaperDubboServiceImpl implements PaperDubboService {

    @Autowired
    PaperService paperService;

    @Autowired
    PaperRecordService paperRecordService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("paper_count", paperService.count());
        }};
    }

    @Override
    public Long joinManual(PaperSettingDto paperSetting, List<QuestionManualDto> settings) throws ServiceException {
        return paperService.createManual(paperSetting, settings);
    }

    @Override
    public Long joinIntelligent(PaperSettingDto paperSetting, List<QuestionIntelligentDto> settings) throws ServiceException {
        return paperService.createIntelligent(paperSetting, settings);
    }

    @Override
    public PaperDetailDto detail(Long paperId) throws ServiceException {
        return MapperUtils.map(paperService.detail(paperId), PaperDetailDto.class);
    }

    @Override
    public Long duplicate(Long paperId) throws ServiceException {
        return paperService.duplicate(paperId);
    }

    @Override
    public Boolean delete(Long paperId) throws ServiceException {
        return paperService.delete(paperId);
    }

    @Override
    public PaperRecordDto getPaperRecord(Long paperId, Long userId) {
        return MapperUtils.map(paperRecordService.getPaperRecord(paperId, userId), PaperRecordDto.class);
    }

    @Override
    public List<PaperRecordDto> getPaperRecords(Long paperId) {
        return paperRecordService.listByPaperIdDubbo(paperId);
    }
}
