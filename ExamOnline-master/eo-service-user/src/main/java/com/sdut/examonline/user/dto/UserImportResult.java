package com.sdut.examonline.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sdut.examonline.common.dto.BaseDto;
import com.sdut.examonline.user.util.GenderConverter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/4/3 11:30
 */
@Data
@ApiModel(value = "导入用户实体", description = "用户修改密码请求")
public class UserImportResult extends BaseDto {

    @ExcelProperty("学号")
    @ApiModelProperty("学号")
    private String userName;

    @ExcelProperty("姓名")
    @ApiModelProperty("姓名")
    private String realName;

    @ExcelProperty("生日")
    @ApiModelProperty("生日")
    private String birthday;

    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    @ApiModelProperty("性别")
    private Integer gender;

    @ExcelProperty("密码")
    @ApiModelProperty("密码")
    private String password;

    @ExcelProperty("导入结果")
    @ApiModelProperty("导入结果")
    private String result;

}
