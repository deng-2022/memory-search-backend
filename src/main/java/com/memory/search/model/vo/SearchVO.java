package com.memory.search.model.vo;

import com.memory.search.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

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
    private List<ArticleVO> articleList;

    /**
     * 诗词搜索列表
     */
    private List<PostVO> postVOList;

    /**
     * 图片搜索列表
     */
    private List<Picture> pictureList;

    /**
     * 聚合搜索结果列表
     */
    private List<?> dataList;

    private static final long serialVersionUID = 1L;
}
