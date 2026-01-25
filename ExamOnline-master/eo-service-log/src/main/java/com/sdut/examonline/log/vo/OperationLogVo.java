package com.sdut.examonline.log.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "操作日志前端展示对象", description = "操作日志前端展示对象")
public class OperationLogVo extends BaseVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "操作者ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "日志消息")
    private String msg;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "请求URI")
    private String uri;

    @ApiModelProperty(value = "请求方法")
    private String method;

    @ApiModelProperty(value = "操作数据")
    private String params;

    @ApiModelProperty(value = "操作类型")
    private Integer type;

    @ApiModelProperty(value = "状态")
    private Integer state;

    @ApiModelProperty(value = "耗时")
    private Long time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

}
