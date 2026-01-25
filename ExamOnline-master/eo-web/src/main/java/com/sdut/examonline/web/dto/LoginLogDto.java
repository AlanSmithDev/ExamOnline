package com.sdut.examonline.web.dto;

import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

import java.util.Date;

@Data
public class LoginLogDto extends BaseDto {

    private Long id;

    private Integer type;

    private String msg;

    private Long userId;

    private String userName;

    private String ip;

    private String browser;

    private String os;

    private Date createTime;

}
