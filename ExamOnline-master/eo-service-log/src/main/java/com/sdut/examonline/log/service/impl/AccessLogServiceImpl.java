package com.sdut.examonline.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.log.entity.AccessLog;
import com.sdut.examonline.log.mapper.AccessLogMapper;
import com.sdut.examonline.log.service.AccessLogService;
import com.sdut.examonline.log.vo.AccessLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @since 2022/4/7 22:42
 */
@Service
public class AccessLogServiceImpl extends ServiceImpl<AccessLogMapper, AccessLog> implements AccessLogService {

    @Override
    public PagingVo<AccessLogVo> page(PagingDto pagingDto) {
        IPage<AccessLog> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<AccessLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(AccessLog::getCreateTime);
        IPage<AccessLog> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), AccessLogVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

}
