package com.memory.search.service;

import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.vo.SearchVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * 图片爬取服务
 */
public interface SearchService {
    SearchVO searchAll(SearchQueryRequest searchQueryRequest, HttpServletRequest request) throws IOException, ExecutionException, InterruptedException;
}
