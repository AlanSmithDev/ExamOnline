package com.sdut.examonline.paper.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.dubbo.dto.PaperRecordDto;
import com.sdut.examonline.paper.dto.PaperRecordExportDto;
import com.sdut.examonline.paper.entity.PaperRecord;
import com.sdut.examonline.paper.vo.PaperRecordDetailVo;
import com.sdut.examonline.paper.vo.PaperRecordVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.vo.PagingVo;

import java.util.List;

public interface PaperRecordService extends IService<PaperRecord> {

    /**
     * 分页查询试卷的成绩记录列表
     *
     * @param paramDto 分页请求
     */
    PagingVo<PaperRecordVo> pageByPaperId(PagingParamDto<Long> paramDto);

    /**
     * 获取试卷的所有成绩记录
     *
     * @param paperId 试卷ID
     */
    List<PaperRecordVo> listByPaperId(Long paperId);

    /**
     * 获取试卷的所有成绩记录(Dubbo)
     *
     * @param paperId 试卷ID
     */
    List<PaperRecordDto> listByPaperIdDubbo(Long paperId);

    /**
     * 分页查询已批改完毕的成绩记录列表
     *
     * @param pagingDto 分页请求
     */
    PagingVo<PaperRecordDetailVo> pageCorrected(PagingDto pagingDto);

    /**
     * 分页查询用户的成绩记录列表
     *
     * @param pagingDto 分页请求
     */
    PagingVo<PaperRecordVo> pageByUserId(PagingDto pagingDto);

    /**
     * 获取用户的所有成绩记录
     *
     * @param userId 用户ID
     */
    List<PaperRecordVo> listByUserId(Long userId);

    /**
     * 获取用户的成绩记录
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     */
    PaperRecord getPaperRecord(Long paperId, Long userId);

    /**
     * 导出试卷成绩
     *
     * @param paperId 试卷ID
     */
    List<PaperRecordExportDto> getExportPaperRecords(Long paperId);

}
