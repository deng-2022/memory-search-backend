package com.memory.search.model.vo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 邓哈哈
 * 2023/8/31 16:31
 * Function:
 * Version 1.0
 */
@Data
public class SearchVO implements Serializable {
    /**
     * 博文搜索列表
     */
    private Page<ArticleVO> articleVOPage;

    /**
     * 诗词搜索列表
     */
    private Page<PostVO> postVOPage;

    /**
     * 图片搜索列表
     */
    private Page<Picture> picturePage;

    /**
     * 聚合搜索结果列表
     */
    private Page<?> dataPage;

    private static final long serialVersionUID = 1L;
}
