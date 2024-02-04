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
     * 搜索词条
     */
    private Long userId;

    /**
     * 搜索词条
     */
    private String searchText;

    /**
     * 搜索词条
     */
    private Integer searchNum;

    // /**
    //  * 搜索日期
    //  */
    // // @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    // private LocalDate sendTime;


    public Message(Long userId, String searchText, Integer searchNum) {
        this.userId = userId;
        this.searchText = searchText;
        this.searchNum = searchNum;
    }

    public Message(Long userId, Integer searchNum) {
        this.userId = userId;
        this.searchNum = searchNum;
    }

    // public Message(Long userId, Integer searchNum, LocalDate sendTime) {
    //     this.userId = userId;
    //     this.searchNum = searchNum;
    //     this.sendTime = sendTime;
    // }
}
