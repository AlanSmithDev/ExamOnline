package com.sdut.examonline.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sdut.examonline.log.entity.LoginLog;
import com.sdut.examonline.log.mapper.LoginLogMapper;
import com.sdut.examonline.log.service.LoginLogService;
import com.sdut.examonline.log.vo.LoginLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.util.MapperUtils;
import com.sdut.examonline.web.vo.PagingVo;
import org.springframework.stereotype.Service;

/**
 * @author Chen
 * @since 2022/4/7 22:42
 */
@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

    @Override
    public PagingVo<LoginLogVo> page(PagingDto pagingDto) {
        IPage<LoginLog> query = new Page<>(pagingDto.getPageNum(), pagingDto.getPageSize());
        LambdaQueryWrapper<LoginLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(LoginLog::getCreateTime);
        IPage<LoginLog> page = super.page(query, wrapper);
        return PagingVo.of(MapperUtils.mapList(page.getRecords(), LoginLogVo.class), page.getTotal(), page.getCurrent(), page.getPages());
    }

}
