package com.sdut.examonline.user.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sdut.examonline.common.dto.BaseDto;
import com.sdut.examonline.user.util.GenderConverter;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/4/3 11:30
 */
@Data
public class UserImportDto extends BaseDto {

    @ExcelProperty("学号")
    private String userName;

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("生日")
    private String birthday;

    @ExcelProperty(value = "性别", converter = GenderConverter.class)
    private Integer gender;

}
