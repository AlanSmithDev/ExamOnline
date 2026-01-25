package com.sdut.examonline.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.util.Date;

@Data
@TableName("eo_file")
public class File extends Model<File> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 后缀名
     */
    private String ext;

    /**
     * 大小
     */
    private Long size;

    /**
     * 上传者
     */
    private Long uploader;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

}
