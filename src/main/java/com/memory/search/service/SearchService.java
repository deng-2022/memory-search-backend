package com.memory.search.service;

import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.entity.Message;
import com.memory.search.model.vo.SearchVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 图片爬取服务
 */
public interface SearchService {
    /**
     * 聚合搜索服务
     *
     * @param searchQueryRequest 聚合搜索词条
     * @param request            request
     * @return 搜索结果
     */
    SearchVO searchAll(SearchQueryRequest searchQueryRequest, HttpServletRequest request);

    /**
     * 搜索建议
     *
     * @param suggestText 聚合搜索词条
     * @return 搜索建议词
     */
    List<String> searchSuggestFromEs(String suggestText);

    /**
     * 记录搜索词条
     *
     * @param suggestText 聚合搜索词条
     * @return 执行结果
     */
    Message setHotWords(String suggestText, HttpServletRequest request);

    /**
     * 获取热搜词
     *
     * @return 执行结果
     */
    List<Message> getHotWords();
}
