package com.sdut.examonline.exam.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data
@TableName("eo_exam_question_intelligent")
public class ExamQuestionIntelligent extends Model<ExamQuestionIntelligent> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 考试ID
     */
    @TableField("exam_id")
    private Long examId;

    /**
     * 题库ID
     */
    @TableField("repo_id")
    private Long repoId;

    /**
     * 试题类型
     */
    @TableField("question_type")
    private Integer questionType;

    /**
     * 抽取数量
     */
    private Integer amount;

    /**
     * 试题分数
     */
    private Integer score;

    /**
     * 试题难度
     */
    private Integer difficulty;

}