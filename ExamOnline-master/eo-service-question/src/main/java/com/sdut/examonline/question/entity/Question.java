package com.sdut.examonline.question.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_question")
public class Question extends Model<Question> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 题目类型
     */
    private Integer type;

    /**
     * 题目难度
     */
    private Integer difficulty;

    /**
     * 题目内容
     */
    private String content;

    /**
     * 音频文件
     */
    @TableField("voice_file")
    private String voiceFile;

    /**
     * 所属题库
     */
    @TableField("repo_id")
    private Long repoId;

    /**
     * 父题目
     */
    @TableField("parent_id")
    private Long parentId = 0L;

    /**
     * 创建者
     */
    private Long creator;

    /**
     * 题目解析
     */
    private String analysis;

    /**
     * 题目解析视频
     */
    @TableField("analysis_video")
    private String analysisVideo;

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