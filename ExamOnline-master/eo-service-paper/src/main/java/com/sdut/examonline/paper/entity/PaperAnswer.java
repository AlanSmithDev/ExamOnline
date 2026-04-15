package com.sdut.examonline.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_paper_answer")
public class PaperAnswer extends Model<PaperAnswer> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 考试ID
     */
    @TableField("paper_id")
    private Long paperId;

    /**
     * 试题ID
     */
    @TableField("question_id")
    private Long questionId;

    /**
     * 提交者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所答内容
     */
    private String content;

    /**
     * 是否答对
     */
    @TableField("is_right")
    private Boolean isRight;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 批改者
     */
    private Long corrector;

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