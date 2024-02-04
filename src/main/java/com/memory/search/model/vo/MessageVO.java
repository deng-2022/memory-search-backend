package com.memory.search.model.vo;

import lombok.Data;

/**
 * @author 邓哈哈
 * 2024/2/3 18:41
 * Function:
 * Version 1.0
 */

@Data
public class MessageVO {
    /**
     * 用户
     */
    private Long userId;

    /**
     * 搜索词条
     */
    private String searchText;

    /**
     * 搜索词条
     */
    private Double searchNum;

    public MessageVO() {
    }

    public MessageVO(Long userId, String searchText, Double searchNum) {
        this.userId = userId;
        this.searchText = searchText;
        this.searchNum = searchNum;
    }
}
