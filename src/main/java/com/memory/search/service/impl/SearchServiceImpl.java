package com.memory.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.common.ErrorCode;
import com.memory.search.dataSource.*;
import com.memory.search.exception.ThrowUtils;
import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.entity.Picture;
import com.memory.search.model.vo.PostVO;
import com.memory.search.model.vo.SearchVO;
import com.memory.search.service.SearchService;
import com.memory.search.model.entity.Article;
import com.memory.search.model.enums.SearchTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author 邓哈哈
 * 2023/8/31 16:35
 * Function:
 * Version 1.0
 */
@Service
public class SearchServiceImpl implements SearchService {
    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
    @Resource
    private ArticleDataSource articleDataSource;
    @Resource
    private DataSourceRegistry dataSourceRegistry;

    @Override
    public SearchVO searchAll(SearchQueryRequest searchQueryRequest, HttpServletRequest request) {
        // 1.校验参数type
        // 1.1.检查type类型
        String type = searchQueryRequest.getType();
        // 1.2.校验参数正确性
        ThrowUtils.throwIf(StringUtils.isEmpty(type), ErrorCode.PARAMS_ERROR);
        // 1.3.获取页面类型
        SearchTypeEnum enumByValue = SearchTypeEnum.getEnumByValue(type);

        String searchText = searchQueryRequest.getSearchText();
        long pageSize = searchQueryRequest.getPageSize();
        long current = searchQueryRequest.getPageNum();

        // 2.执行查询全部数据
        SearchVO searchVO = null;

        if (enumByValue == null) {
            // 获取文章
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() ->
                    postDataSource.search(searchText, pageSize, current));

            // 获取图片
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() ->
                    pictureDataSource.search(searchText, pageSize, current));

            // 获取博文
            CompletableFuture<Page<Article>> articleTask = CompletableFuture.supplyAsync(() ->
                    articleDataSource.search(searchText, pageSize, current));

            CompletableFuture.allOf(postTask, pictureTask, articleTask).join();

            try {
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<Article> articlePage = articleTask.get();

                searchVO = new SearchVO();
                searchVO.setPostVOList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
                searchVO.setArticleList(articlePage.getRecords());

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 分类查询
            searchVO = new SearchVO();
            DataSource<?> dataSourceByType = dataSourceRegistry.getDataSourceByType(type);
            try {
                Page<?> page = dataSourceByType.search(searchText, pageSize, current);
                searchVO.setDataList(page.getRecords());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return searchVO;
    }
}
