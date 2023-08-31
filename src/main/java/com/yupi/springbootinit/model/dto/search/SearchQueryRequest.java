package com.yupi.springbootinit.model.dto.search;

import com.yupi.springbootinit.common.PageRequest;
import lombok.Data;

/**
 * @author 邓哈哈
 * 2023/8/31 16:26
 * Function:
 * Version 1.0
 */
@Data
public class SearchQueryRequest extends PageRequest {
    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}
