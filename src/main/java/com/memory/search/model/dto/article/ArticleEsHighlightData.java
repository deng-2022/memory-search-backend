package com.memory.search.model.dto.article;

import lombok.Data;

/**
 * 搜索关键词高亮
 *
 * @author lacy
 */

@Data
public class ArticleEsHighlightData {
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 摘要
     */
    private String description;

    /**
     * 内容
     */
    private String content;
}
