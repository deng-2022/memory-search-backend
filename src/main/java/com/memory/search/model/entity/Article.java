package com.memory.search.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 博文
 *
 * @TableName article
 */
@TableName(value = "article")
@Data
public class Article implements Serializable {
    /**
     * 文章id
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 作者id
     */
    private Long userId;

    /**
     * 文章内容
     */
    private byte[] content;

    /**
     * 创作者
     */
    private Long authorId;

    /**
     * 文章类型
     */
    private Integer type;

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
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer isDelete;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}