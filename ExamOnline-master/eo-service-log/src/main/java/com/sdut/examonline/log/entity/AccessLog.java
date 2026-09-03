package com.sdut.examonline.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_log_access")
public class AccessLog extends Model<AccessLog> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 用户代理
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 请求URI
     */
    private String uri;

    /**
     * 操作方法
     */
    private String method;

    /**
     * 请求参数
     */
    private String params;

    /**
     * 响应状态码
     */
    @TableField("resp_code")
    private Integer respCode;

    /**
     * 响应信息
     */
    @TableField("resp_body")
    private String respBody;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 耗时
     */
    private Long time;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

}
