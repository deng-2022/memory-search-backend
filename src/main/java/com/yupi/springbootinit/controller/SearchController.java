package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.model.dto.search.SearchQueryRequest;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.vo.SearchVO;
import com.yupi.springbootinit.service.PictureService;
import com.yupi.springbootinit.service.PostService;
import com.yupi.springbootinit.service.SearchService;
import com.yupi.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 邓哈哈
 * 2023/8/31 16:25
 * Function:
 * Version 1.0
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {
    @Resource
    private SearchService searchService;


    @PostMapping("/list/page/vo")
    public BaseResponse<SearchVO> listPictureByPage(@RequestBody SearchQueryRequest searchQueryRequest,
                                                    HttpServletRequest request) throws IOException {
        //controller层对参数的校验
        long pageSize = searchQueryRequest.getPageSize();
        long current = searchQueryRequest.getCurrent();
        String searchText = searchQueryRequest.getSearchText();

        SearchVO searchVO = searchService.searchAll(searchText, pageSize, current, request);

        return ResultUtils.success(searchVO);
    }
}
