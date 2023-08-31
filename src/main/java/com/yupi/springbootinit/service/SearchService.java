package com.yupi.springbootinit.service;

import com.yupi.springbootinit.model.vo.SearchVO;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 图片爬取服务
 */
public interface SearchService {
    SearchVO searchAll(String searchText, long pageSize, long currentPage, HttpServletRequest request) throws IOException;
}
