package com.memory.search.common;

import com.memory.search.constant.CommonConstant;
import lombok.Data;

/**
 * 分页请求
 *
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private long pageNum = 1;

    /**
     * 页面大小
     */
    private long pageSize = 30;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认升序）
     */
    private String sortOrder = CommonConstant.SORT_ORDER_ASC;
}
