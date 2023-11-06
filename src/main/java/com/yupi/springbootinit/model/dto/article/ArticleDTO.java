package com.yupi.springbootinit.model.dto.article;

import com.yupi.springbootinit.model.dto.common.CommonDTO;
import lombok.Data;

/**
 * @author 邓哈哈
 * 2023/9/18 13:19
 * Function:
 * Version 1.0
 */
@Data
public class ArticleDTO extends CommonDTO {
    /**
     * 文章id
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String description;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 浏览量
     */
    private Integer view;

    /**
     * 点赞量
     */
    private Integer likes;

    /**
     * 评论量
     */
    private String comments;

    /**
     * 创作者
     */
    private Long authorId;

    /**
     * 收藏量
     */
    private Integer collects;

    /**
     * 封面图片
     */
    private String articleUrl;

    /**
     * 文章标签
     */
    private String tags;
}
