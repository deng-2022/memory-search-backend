package com.memory.search.controller;

import com.memory.search.common.BaseResponse;
import com.memory.search.common.ResultUtils;
import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.vo.SearchVO;
import com.memory.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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

    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest,
                                            HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        //controller层对参数的校验

        SearchVO searchVO = searchService.searchAll(searchQueryRequest, request);
        return ResultUtils.success(searchVO);
    }
}
