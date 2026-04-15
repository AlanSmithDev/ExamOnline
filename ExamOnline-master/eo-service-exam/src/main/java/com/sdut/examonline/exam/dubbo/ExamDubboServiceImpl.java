package com.sdut.examonline.exam.dubbo;

import com.sdut.examonline.dubbo.client.ExamDubboService;
import com.sdut.examonline.dubbo.dto.ExamDto;
import com.sdut.examonline.dubbo.dto.ExamQuestionIntelligentDto;
import com.sdut.examonline.dubbo.dto.ExamQuestionManualDto;
import com.sdut.examonline.exam.service.ExamQuestionIntelligentService;
import com.sdut.examonline.exam.service.ExamQuestionManualService;
import com.sdut.examonline.exam.service.ExamService;
import com.sdut.examonline.web.util.MapperUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 11:29
 */
@Service(retries = 0)
public class ExamDubboServiceImpl implements ExamDubboService {

    @Autowired
    ExamService examService;

    @Autowired
    ExamQuestionManualService examQuestionManualService;

    @Autowired
    ExamQuestionIntelligentService examQuestionIntelligentService;

    @Override
    public Map<String, Object> getAdminDashboard() {
        return new HashMap<String, Object>(){{
            put("exam_count", examService.count());
        }};
    }

    @Override
    public ExamDto getExamById(Long examId) {
        return MapperUtils.map(examService.getExamByIdInner(examId), ExamDto.class);
    }

    @Override
    public List<ExamQuestionManualDto> allManuals(Long examId) {
        return MapperUtils.mapList(examQuestionManualService.allManualsInner(examId), ExamQuestionManualDto.class);
    }

    @Override
    public List<ExamQuestionIntelligentDto> allIntelligents(Long examId) {
        return MapperUtils.mapList(examQuestionIntelligentService.allIntelligentsInner(examId), ExamQuestionIntelligentDto.class);
    }

}
