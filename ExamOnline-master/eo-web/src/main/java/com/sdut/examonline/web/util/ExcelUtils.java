package com.sdut.examonline.web.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import org.apache.commons.compress.utils.Lists;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Chen
 * @since 2022/4/2 17:42
 */
public class ExcelUtils {

    public static <T> List<T> readExcel(InputStream inputStream, Class<T> clazz) {
        List<T> list = Lists.newArrayList();
        EasyExcel.read(inputStream)
                .head(clazz)
                .sheet().registerReadListener(new ReadListener<T>() {

            @Override
            public void invoke(T data, AnalysisContext analysisContext) {
                list.add(data);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }

        }).doRead();
        return list;
    }

    public static <T> void writeExcel(OutputStream outputStream, List<T> rows, Class<?> clazz) {
        EasyExcel.write(outputStream)
                .autoCloseStream(false)
                .head(clazz)
                .sheet().doWrite(rows);
    }

}
