package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.dataSource.*;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.dto.post.PostQueryRequest;
import com.yupi.springbootinit.model.dto.search.SearchQueryRequest;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.model.enums.SearchTypeEnum;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.SearchService;
import com.yupi.springbootinit.service.UserService;
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
    private UserDataSource userDataSource;
    @Resource
    private PostDataSource postDataSource;
    @Resource
    private PictureDataSource pictureDataSource;
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
        long current = searchQueryRequest.getCurrent();

        // 2.执行查询全部数据
        SearchVO searchVO = null;

        if (enumByValue == null) {
            // 获取文章
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() ->
                    postDataSource.search(searchText, pageSize, current));

            // 获取用户
            CompletableFuture<Page<User>> userTask = CompletableFuture.supplyAsync(() ->
                    userDataSource.search(searchText, pageSize, current));

            // 获取图片
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() ->
                    pictureDataSource.search(searchText, pageSize, current));

            CompletableFuture.allOf(userTask, postTask, pictureTask).join();

            try {
                Page<PostVO> postVOPage = postTask.get();
                Page<User> userPage = userTask.get();
                Page<Picture> picturePage = pictureTask.get();

                searchVO = new SearchVO();
                searchVO.setUserList(userPage.getRecords());
                searchVO.setPostList(postVOPage.getRecords());
                searchVO.setPictureList(picturePage.getRecords());
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
