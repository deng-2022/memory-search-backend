package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.springbootinit.model.dto.article.ArticleQueryRequest;
import com.yupi.springbootinit.model.entity.Article;

/**
 * @author Lenovo
 * @description 针对表【article(博文)】的数据库操作Service
 * @createDate 2023-11-06 21:28:22
 */
public interface ArticleService extends IService<Article> {
    /**
     * 从 ES 查询
     *
     * @param articleQueryRequest 博文查询请求参数
     * @return 博文列表
     */
    Page<Article> searchFromEs(ArticleQueryRequest articleQueryRequest);
}
