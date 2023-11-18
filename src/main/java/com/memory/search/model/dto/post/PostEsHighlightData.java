package com.memory.search.model.dto.post;

import lombok.Data;

/**
 * 搜索关键词高亮
 * @author lacy
 */

@Data
public class PostEsHighlightData {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 诗人/词人
     */
    private String author;

    /**
     * 内容
     */
    private String content;
}
