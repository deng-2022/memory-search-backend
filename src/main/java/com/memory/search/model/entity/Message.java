package com.memory.search.model.entity;

import lombok.Data;

/**
 * @author 邓哈哈
 * 2024/2/3 18:41
 * Function:
 * Version 1.0
 */

@Data
public class Message {
    /**
     * 用户
     */
    private Long userId;

    /**
     * 搜索词条
     */
    private String searchText;


    public Message(Long userId, String searchText) {
        this.userId = userId;
        this.searchText = searchText;
    }
}
