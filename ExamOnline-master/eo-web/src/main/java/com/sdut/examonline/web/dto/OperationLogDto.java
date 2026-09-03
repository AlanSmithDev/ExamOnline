package com.sdut.examonline.web.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.Date;

@Data
public class OperationLogDto extends BaseDto {

    private Long id;

    private Long userId;

    private String userName;

    private String ip;

    private Integer type;

    private String msg;

    private String userAgent;

    private String uri;

    private String method;

    private String params;

    private Integer state;

    private Long time;

    private Date createTime;

}
