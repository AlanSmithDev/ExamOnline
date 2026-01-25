package com.sdut.examonline.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_question_answer")
public class QuestionAnswer extends Model<QuestionAnswer> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 答案内容
     */
    private String content;

    /**
     * 是否为正确答案
     */
    @TableField("is_right")
    private Boolean isRight;

    /**
     * 所属题库
     */
    @TableField("question_id")
    private Long questionId;

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