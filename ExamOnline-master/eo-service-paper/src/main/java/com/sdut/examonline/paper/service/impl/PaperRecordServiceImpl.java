package com.sdut.examonline.paper.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.sdut.examonline.dubbo.dto.PaperRecordDto;
import com.sdut.examonline.common.constant.PaperRecordState;
import com.sdut.examonline.paper.constant.PaperState;
import com.sdut.examonline.dubbo.client.UserDubboService;
import com.sdut.examonline.dubbo.dto.UserDto;
import com.sdut.examonline.paper.dto.PaperRecordExportDto;
import com.sdut.examonline.paper.entity.PaperRecord;
import com.sdut.examonline.paper.mapper.PaperRecordMapper;
import com.sdut.examonline.paper.service.PaperRecordService;
import com.sdut.examonline.paper.service.PaperService;
import com.sdut.examonline.paper.vo.PaperRecordDetailVo;
import com.sdut.examonline.paper.vo.PaperRecordVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.dto.PagingParamDto;
import com.sdut.examonline.web.util.JwtUtils;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Chen
 * @since 2022/3/30 11:33
 */
@Service
public class PaperRecordServiceImpl extends ServiceImpl<PaperRecordMapper, PaperRecord> implements PaperRecordService {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    PaperService paperService;

    @Reference(check = false)
    UserDubboService userDubboService;

    @Override
    public PagingVo<PaperRecordVo> pageByPaperId(PagingParamDto<Long> paramDto) {
        IPage<PaperRecord> query = new Page<>(paramDto.getPageNum(), paramDto.getPageSize());
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getPaperId, paramDto.getParam());
        IPage<PaperRecord> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), PaperRecordVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public List<PaperRecordVo> listByPaperId(Long paperId) {
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getPaperId, paperId);
        return MapperUtils.mapList(super.list(wrapper), PaperRecordVo.class);
    }

    @Override
    public List<PaperRecordDto> listByPaperIdDubbo(Long paperId) {
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getPaperId, paperId);
        return MapperUtils.mapList(super.list(wrapper), PaperRecordDto.class);
    }

    @Override
    public PagingVo<PaperRecordDetailVo> pageCorrected(PagingDto pagingDto) {
        IPage<PaperRecord> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getUserId, JwtUtils.getUserId())
                .eq(PaperRecord::getState, PaperState.CORRECT_COMPLETED);
        IPage<PaperRecord> page = super.page(query, wrapper);
        List<PaperRecordDetailVo> voList = MapperUtils.mapList(page.getRecords(), PaperRecordDetailVo.class);
        for (PaperRecordDetailVo vo : voList) {
            vo.setPaper(paperService.get(vo.getId()));
        }
        return PagingVo.of(voList, page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public PagingVo<PaperRecordVo> pageByUserId(PagingDto pagingDto) {
        IPage<PaperRecord> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getUserId, JwtUtils.getUserId());
        IPage<PaperRecord> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), PaperRecordVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

    @Override
    public List<PaperRecordVo> listByUserId(Long userId) {
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getUserId, userId);
        return MapperUtils.mapList(super.list(wrapper), PaperRecordVo.class);
    }

    @Override
    public PaperRecord getPaperRecord(Long paperId, Long userId) {
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getPaperId, paperId)
                .eq(PaperRecord::getUserId, userId);
        return super.getOne(wrapper, false);
    }

    @Override
    public List<PaperRecordExportDto> getExportPaperRecords(Long paperId) {
        LambdaQueryWrapper<PaperRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PaperRecord::getPaperId, paperId);
        List<PaperRecord> records = super.list(wrapper);
        if (CollectionUtils.isEmpty(records)) {
            return Lists.newArrayList();
        }
        List<UserDto> users = userDubboService.getUserByIdBatch(records.stream().map(PaperRecord::getUserId).collect(Collectors.toList()));
        List<PaperRecordExportDto> list = Lists.newArrayListWithExpectedSize(records.size());
        StringBuilder sb;
        for (PaperRecord paperRecord : records) {
            PaperRecordExportDto dto = new PaperRecordExportDto();
            UserDto user = null;
            for (UserDto userDto : users) {
                if (userDto.getId().equals(paperRecord.getUserId())) {
                    user = userDto;
                    break;
                }
            }
            if (user == null) {
                continue;
            }
            dto.setUserName(user.getUserName());
            dto.setRealName(user.getRealName());
            dto.setStartTime(DATE_FORMAT.format(paperRecord.getStartTime()));
            dto.setEndTime(DATE_FORMAT.format(paperRecord.getEndTime()));
            dto.setScore(paperRecord.getScore());
            if (PaperRecordState.PREPARED.equals(paperRecord.getState())) {
                dto.setDuration("缺考");
            } else {
                int diff = (int) ((paperRecord.getEndTime().getTime() - paperRecord.getStartTime().getTime()) / 1000);
                int hour = diff / (60 * 60);
                int min = diff % (60 * 60) / 60;
                int second = diff % (60 * 60) % 60;
                sb = new StringBuilder();
                if (hour > 0) {
                    sb.append(hour).append("小时");
                }
                if (min > 0) {
                    sb.append(min).append("分钟");
                }
                if (second > 0) {
                    sb.append(second).append("秒");
                }
                dto.setDuration(sb.toString());
            }
            list.add(dto);
        }
        return list;
    }

}
