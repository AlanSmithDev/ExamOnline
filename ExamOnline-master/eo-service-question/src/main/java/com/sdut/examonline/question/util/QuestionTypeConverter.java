package com.sdut.examonline.question.util;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.apache.commons.lang3.StringUtils;

public class QuestionTypeConverter implements Converter<Integer> {

    public QuestionTypeConverter() {

    }

    @Override
    public Class<?> supportJavaTypeKey() {
        return Integer.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        String excelValue = cellData.getStringValue();
        if (StringUtils.isEmpty(excelValue)) {
            return 0;
        }
        if (excelValue.contains("单选")) {
            return 0;
        } else if (excelValue.contains("多选")) {
            return 1;
        } else {
            return 2;
        }
    }

}