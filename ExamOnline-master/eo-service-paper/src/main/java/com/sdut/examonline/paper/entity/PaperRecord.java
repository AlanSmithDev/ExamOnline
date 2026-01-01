package com.sdut.examonline.paper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_paper_record")
public class PaperRecord extends Model<PaperRecord> {

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
     * 答题者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 状态
     */
    private Integer state;

    /**
     * 开始答题时间
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * 交卷时间
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