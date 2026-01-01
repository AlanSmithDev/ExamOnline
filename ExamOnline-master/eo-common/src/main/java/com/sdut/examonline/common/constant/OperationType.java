package com.sdut.examonline.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Chen
 * @since 2022/4/8 8:47
 */
@Getter
@RequiredArgsConstructor
public enum OperationType {

    /**
     * 其他
     */
    OTHER(0),

    /**
     * 新建
     */
    CREATE(1),

    /**
     * 删除
     */
    DELETE(2),

    /**
     * 修改
     */
    UPDATE(3),

    /**
     * 查询
     */
    QUERY(4),

    /**
     * 导入
     */
    IMPORT(5),

    /**
     * 导出
     */
    EXPORT(6);

    private final Integer value;

}
