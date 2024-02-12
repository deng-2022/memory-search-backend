package com.memory.search.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author 邓哈哈
 * 2023/9/18 13:19
 * Function:
 * Version 1.0
 */
@Data
public class ArticleVO {
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
     * 作者id
     */
    private Long userId;

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
     * 收藏量
     */
    private Integer collects;

    /**
     * 封面图片
     */
    private String articleUrl;

    /**
     * 作者信息
     */
    private UserVO userVO;

    /**
     * 文章标签
     */
    private String tags;

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
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
