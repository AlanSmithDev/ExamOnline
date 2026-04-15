package com.sdut.examonline.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_paper")
public class Paper extends Model<Paper> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 总分
     */
    @TableField("total_score")
    private Integer totalScore;

    /**
     * 是否限时
     */
    @TableField("time_limit")
    private Boolean timeLimit;

    /**
     * 是否人脸识别
     */
    @TableField("face_check")
    private Boolean faceCheck;

    /**
     * 限时进入(分钟,0为不限制)
     */
    @TableField("join_limit")
    private Integer joinLimit;

    /**
     * 限时提交(分钟,0为不限制)
     */
    @TableField("submit_limit")
    private Integer submitLimit;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private Date endTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
}