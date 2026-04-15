package com.sdut.examonline.web.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.Date;

@Data
public class AccessLogDto extends BaseDto {

    private Long id;

    private Long userId;

    private String userName;

    private String ip;

    private String userAgent;

    private String uri;

    private String method;

    private String params;

    private Integer respCode;

    private String respBody;

    private String error;

    private Long time;

    private Date createTime;

}
