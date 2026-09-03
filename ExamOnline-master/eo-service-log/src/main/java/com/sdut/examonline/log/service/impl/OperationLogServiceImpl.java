package com.sdut.examonline.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.log.entity.OperationLog;
import com.sdut.examonline.log.mapper.OperationLogMapper;
import com.sdut.examonline.log.service.OperationLogService;
import com.sdut.examonline.log.vo.OperationLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @since 2022/4/7 22:42
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public PagingVo<OperationLogVo> page(PagingDto pagingDto) {
        IPage<OperationLog> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(OperationLog::getCreateTime);
        IPage<OperationLog> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), OperationLogVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

}
