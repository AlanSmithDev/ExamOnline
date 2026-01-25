package com.sdut.examonline.common.util;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Chen
 * @since 2022/3/12 11:24
 */
public class RandomUtils {

    private static final Random RANDOM = new SecureRandom();

    public static <T> List<T> randomElements(List<T> records, int size) {
        Assert.isTrue(size <= records.size(), 400, "抽取数量大于集合大小");
        List<T> list = new ArrayList<>();
        while (list.size() < size) {
            T data = records.get(RANDOM.nextInt(records.size()));
            while (list.contains(data)) {
                data = records.get(RANDOM.nextInt(records.size()));
            }
            list.add(data);
        }
        return list;
    }

}
