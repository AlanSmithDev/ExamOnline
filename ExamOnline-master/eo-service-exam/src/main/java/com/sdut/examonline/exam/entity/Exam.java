package com.sdut.examonline.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_exam")
public class Exam extends Model<Exam> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 组卷方式(0手动组卷,1智能组卷)
     */
    @TableField("join_type")
    private Integer joinType;

    /**
     * ID
     */
    @TableField("paper_id")
    private Long paperId;

    /**
     * 考试标题
     */
    private String title;

    /**
     * 考试内容
     */
    private String content;

    /**
     * 状态
     */
    private Integer state;

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
     * 考试时长(分钟)
     */
    private Integer duration;

    /**
     * 考试班级
     */
    @TableField("depart_ids")
    private String departIds;

    /**
     * 创建者
     */
    private Long creator;

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