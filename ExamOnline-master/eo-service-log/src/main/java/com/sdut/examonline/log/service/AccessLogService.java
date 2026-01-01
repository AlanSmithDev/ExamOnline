package com.sdut.examonline.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.log.entity.AccessLog;
import com.sdut.examonline.log.vo.AccessLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;

public interface AccessLogService extends IService<AccessLog> {

    PagingVo<AccessLogVo> page(PagingDto pagingDto);

}
