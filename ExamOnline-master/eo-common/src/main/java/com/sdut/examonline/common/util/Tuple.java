package com.sdut.examonline.common.util;

import lombok.Data;

/**
 * 二元组
 *
 * @author Chen
 * @since 2022/3/14 16:14
 */
@Data
public class Tuple<A, B> {

    private A a;

    private B b;

    public Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Tuple<A, B> of(A a, B b) {
        return new Tuple<>(a, b);
    }

}
