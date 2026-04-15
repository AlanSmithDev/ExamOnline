package com.sdut.examonline.paper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.dubbo.dto.PaperSettingDto;
import com.sdut.examonline.dubbo.dto.QuestionDto;
import com.sdut.examonline.dubbo.dto.QuestionIntelligentDto;
import com.sdut.examonline.dubbo.dto.QuestionManualDto;
import com.sdut.examonline.paper.entity.Paper;
import com.sdut.examonline.paper.vo.PaperDetailVo;
import com.sdut.examonline.paper.vo.PaperRecordVo;
import com.sdut.examonline.paper.vo.PaperVo;

import java.util.List;
import java.util.Map;

public interface PaperService extends IService<Paper> {

    /**
     * 创建试卷，手动组卷
     *
     * @param paperSetting 试卷设定
     * @param settings     组卷设定
     */
    Long createManual(PaperSettingDto paperSetting, List<QuestionManualDto> settings);

    /**
     * 创建试卷，智能组卷
     *
     * @param paperSetting 试卷设定
     * @param settings     组卷设定
     */
    Long createIntelligent(PaperSettingDto paperSetting, List<QuestionIntelligentDto> settings);

    /**
     * 获取试卷
     *
     * @param paperId 试卷ID
     */
    PaperVo get(Long paperId);

    /**
     * 获取试卷详情(包括所有试题)
     *
     * @param paperId 试卷ID
     */
    PaperDetailVo detail(Long paperId);

    /**
     * 复制试卷
     *
     * @param paperId 试卷ID
     */
    Long duplicate(Long paperId);

    /**
     * 删除试卷
     *
     * @param paperId 试卷ID
     */
    Boolean delete(Long paperId);

    /**
     * 开始答题
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     */
    PaperRecordVo startAnswer(Long paperId, Long userId);

    /**
     * 是否需要人脸识别
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     */
    Boolean needCheckFace(Long paperId, Long userId);

    /**
     * 提交试卷
     *
     * @param paperId 试卷ID
     */
    PaperRecordVo submit(Long paperId);

    /**
     * 自动批改试卷
     * 仅批改支持自动批改的题型
     * <p>
     * 注意：此方法仅用于批改提前交卷的试卷
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     */
    void correct(Long paperId, Long userId);

    /**
     * 自动批改所有试卷
     * 仅批改支持自动批改的题型
     *
     * @param paperId 试卷ID
     */
    void correctPaper(Long paperId);

    /**
     * 获取需要手动批改的题目(内部调用)
     *
     * @param paperId 试卷ID
     */
    List<QuestionDto> getManualCorrectQuestions(Long paperId);

    /**
     * 导出试卷
     *
     * @param paperId 试卷ID
     */
    Map<String, Object> exportPaper(Long paperId);
}
