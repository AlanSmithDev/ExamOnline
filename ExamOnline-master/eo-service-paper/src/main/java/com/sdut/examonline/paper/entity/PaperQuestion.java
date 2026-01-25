package com.sdut.examonline.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data
@TableName("eo_paper_question")
public class PaperQuestion extends Model<PaperQuestion> {

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
     * 试题分数
     */
    private Integer score;

}