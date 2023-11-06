package com.yupi.springbootinit.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.dto.article.ArticleQueryRequest;
import com.yupi.springbootinit.model.entity.Article;
import com.yupi.springbootinit.service.ArticleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 邓哈哈
 * 2023/9/2 20:05
 * Function:
 * Version 1.0
 */

/**
 * 博文搜索 适配实现
 */
@Service
public class ArticleDataSource implements DataSource<Article> {
    @Resource
    private ArticleService articleService;

    @Override
    public Page<Article> search(String searText, long pageSize, long current) {
        ArticleQueryRequest articleQueryRequest = new ArticleQueryRequest();

        articleQueryRequest.setSearchText(searText);
        articleQueryRequest.setPageSize(pageSize);
        articleQueryRequest.setPageNum(current);

        return articleService.searchFromEs(articleQueryRequest);
    }
}

