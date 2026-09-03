package com.sdut.examonline.depart.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

@Data
@TableName("eo_depart_member")
public class DepartMember extends Model<DepartMember> {

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 班级ID
     */
    @TableField("depart_id")
    private Long departId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

}