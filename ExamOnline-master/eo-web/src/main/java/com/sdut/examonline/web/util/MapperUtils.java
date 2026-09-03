package com.sdut.examonline.web.util;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 对象拷贝
 * <p>
 * 尽量少使用,反射耗时
 *
 * @author Chen
 * @since 2022/3/13 18:29
 */
public class MapperUtils {

    private static final Mapper MAPPER = DozerBeanMapperBuilder.buildDefault();

    public static <T> T map(Object source, Class<T> destinationClass) {
        if (source == null) {
            return null;
        }
        return MAPPER.map(source, destinationClass);
    }

    public static void map(Object source, Object destination) {
        if (source == null || destination == null) {
            return;
        }
        MAPPER.map(source, destination);
    }

    public static <T> List<T> mapList(Collection<?> sourceList, Class<T> destinationClass) {
        List<T> destinationList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            destinationList.add(map(sourceObject, destinationClass));
        }
        return destinationList;
    }

}
