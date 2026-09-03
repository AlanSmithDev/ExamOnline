package com.sdut.examonline.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.log.entity.LoginLog;
import com.sdut.examonline.log.vo.LoginLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;

public interface LoginLogService extends IService<LoginLog> {

    PagingVo<LoginLogVo> page(PagingDto pagingDto);

}
