package com.sdut.examonline.common.constant;

/**
 * @author Chen
 * @since 2022/3/31 16:32
 */
public class CacheConstants {

    /**
     * Caffeine过期时间(秒)
     */
    public static final Integer CAFFEINE_EXPIRE_SECONDS = 5 * 60;

    /**
     * Caffeine初始容量
     */
    public static final Integer CAFFEINE_INITIAL_CAPACITY = 36;

    /**
     * Caffeine最大容量
     */
    public static final Integer CAFFEINE_MAXIMUM_SIZE = 2048;

    /**
     * 空对象字符串
     */
    public static final String EMPTY_OBJECT_VALUE = "{}";

    /**
     * 空对象
     */
    public static final Object EMPTY_OBJECT = new Object();

    /**
     * 空字符串
     */
    public static final String NULL_STRING_VALUE = "null";

}
