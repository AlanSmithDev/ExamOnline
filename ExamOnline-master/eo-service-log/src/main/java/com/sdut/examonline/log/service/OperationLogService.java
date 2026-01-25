package com.sdut.examonline.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sdut.examonline.log.entity.OperationLog;
import com.sdut.examonline.log.vo.OperationLogVo;
import com.sdut.examonline.web.dto.PagingDto;
import com.sdut.examonline.web.vo.PagingVo;

public interface OperationLogService extends IService<OperationLog> {

    PagingVo<OperationLogVo> page(PagingDto pagingDto);

}
