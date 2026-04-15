package com.sdut.examonline.paper.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sdut.examonline.common.dto.BaseDto;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/4/3 11:30
 */
@Data
public class PaperRecordExportDto extends BaseDto {

    @ExcelProperty("用户名")
    private String userName;

    @ExcelProperty("姓名")
    private String realName;

    @ExcelProperty("开始答题时间")
    private String startTime;

    @ExcelProperty("结束答题时间")
    private String endTime;

    @ExcelProperty("答题时长")
    private String duration;

    @ExcelProperty("分数")
    private Integer score;

}
