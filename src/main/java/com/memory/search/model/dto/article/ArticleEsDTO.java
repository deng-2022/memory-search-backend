package com.memory.search.model.dto.article;

import com.google.gson.Gson;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 博文 ES 包装类
 * @author memory
 **/
@Document(indexName = "article_v4")
@Data
public class ArticleEsDTO implements Serializable {
    private static final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 文章id
     */
    @Id
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
     * 文章类型
     */
    private Integer type;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 创作者
     */
    private Long authorId;

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

    /**
     * 创建时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date createTime;

    /**
     * 更新时间
     */
    @Field(index = false, store = true, type = FieldType.Date, format = {}, pattern = DATE_TIME_PATTERN)
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    private static final long serialVersionUID = 1L;

    private static final Gson GSON = new Gson();
}
