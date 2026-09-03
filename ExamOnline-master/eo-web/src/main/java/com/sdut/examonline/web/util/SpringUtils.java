package com.sdut.examonline.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Chen
 * @since 2022/4/8 21:45
 */
@Component
public class SpringUtils {

    private static ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext context) {
        SpringUtils.context = context;
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }
}
