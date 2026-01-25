package com.sdut.examonline.log.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sdut.examonline.common.vo.BaseVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@ApiModel(value = "访问日志前端展示对象", description = "访问日志前端展示对象")
public class AccessLogVo extends BaseVo {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    @ApiModelProperty(value = "请求URI")
    private String uri;

    @ApiModelProperty(value = "操作方法")
    private String method;

    @ApiModelProperty(value = "请求数据")
    private String params;

    @ApiModelProperty(value = "响应状态码")
    private Integer respCode;

    @ApiModelProperty(value = "响应信息")
    private String respBody;

    @ApiModelProperty(value = "错误信息")
    private String error;

    @ApiModelProperty(value = "耗时")
    private Long time;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间", example = "2022-03-10 00:00:00")
    private Date createTime;

}
