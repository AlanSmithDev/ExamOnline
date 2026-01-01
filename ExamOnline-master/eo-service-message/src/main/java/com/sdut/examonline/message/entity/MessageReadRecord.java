package com.sdut.examonline.message.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_message_record")
public class MessageReadRecord extends Model<MessageReadRecord> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 消息ID
     */
    @TableField("mid")
    private Long mId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否阅读
     */
    private Boolean isRead;

    /**
     * 阅读时间
     */
    @TableField("read_time")
    private Date readTime;

    /**
     * 是否删除
     */
    private Boolean isDelete;

    /**
     * 删除时间
     */
    @TableField("read_time")
    private Date deleteTime;

}
