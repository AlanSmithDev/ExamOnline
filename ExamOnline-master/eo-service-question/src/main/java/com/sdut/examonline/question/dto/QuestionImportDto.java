package com.sdut.examonline.question.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.sdut.examonline.common.dto.BaseDto;
import com.sdut.examonline.question.util.QuestionTypeConverter;
import lombok.Data;

/**
 * @author Chen
 * @since 2022/4/6 19:47
 */
@Data
public class QuestionImportDto extends BaseDto {

    @ExcelProperty(value = "类型", converter = QuestionTypeConverter.class)
    private Integer type;

    @ExcelProperty("难度")
    private Integer difficulty;

    @ExcelProperty("题干")
    private String content;

    @ExcelProperty("解析")
    private String analysis;

    @ExcelProperty("选项")
    private String options;

    @ExcelProperty("正确选项")
    private String correctOptions;

}
