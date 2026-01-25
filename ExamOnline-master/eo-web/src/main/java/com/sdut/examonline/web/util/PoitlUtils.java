package com.sdut.examonline.web.util;

import com.deepoove.poi.XWPFTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * @author Chen
 * @since 2022/4/6 22:01
 */
public class PoitlUtils {

    public static void writeWord(InputStream templateInpupt, OutputStream resultOut, Map<String, Object> options) throws IOException {
        XWPFTemplate template = XWPFTemplate.compile(templateInpupt).render(options);
        template.write(resultOut);
    }

}
