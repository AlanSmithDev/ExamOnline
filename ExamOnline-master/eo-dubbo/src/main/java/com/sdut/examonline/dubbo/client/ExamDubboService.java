package com.sdut.examonline.dubbo.client;

import com.sdut.examonline.dubbo.dto.ExamDto;
import com.sdut.examonline.dubbo.dto.ExamQuestionIntelligentDto;
import com.sdut.examonline.dubbo.dto.ExamQuestionManualDto;

import java.util.List;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/3/17 10:26
 */
public interface ExamDubboService {

    Map<String, Object> getAdminDashboard();

    ExamDto getExamById(Long examId);

    List<ExamQuestionManualDto> allManuals(Long examId);

    List<ExamQuestionIntelligentDto> allIntelligents(Long examId);

}
