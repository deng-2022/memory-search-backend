package com.memory.search.model.entity;

import lombok.Data;

/**
 * @author 邓哈哈
 * 2023/8/29 21:42
 * Function:
 * Version 1.0
 */

/**
 * 图片类
 */
@Data
public class Picture {
    public Picture(String title, String url) {
        this.title = title;
        this.url = url;
    }

    private String title;
    private String url;
}
