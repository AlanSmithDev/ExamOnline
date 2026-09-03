package com.sdut.examonline.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_log_operation")
public class OperationLog extends Model<OperationLog> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 操作者ID
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
     * 日志消息
     */
    private String msg;

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
     * 请求方法
     */
    private String method;

    /**
     * 操作数据
     */
    private String params;

    /**
     * 操作类型
     */
    private Integer type;

    /**
     * 状态
     */
    private Integer state;

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
