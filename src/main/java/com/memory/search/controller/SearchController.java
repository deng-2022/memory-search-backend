package com.memory.search.controller;

import com.memory.search.common.BaseResponse;
import com.memory.search.common.ResultUtils;
import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.entity.Message;
import com.memory.search.model.vo.MessageVO;
import com.memory.search.model.vo.SearchVO;
import com.memory.search.service.SearchService;
import com.qcloud.cos.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
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
        // controller层对参数的校验

        SearchVO searchVO = searchService.searchAll(searchQueryRequest, request);
        return ResultUtils.success(searchVO);
    }

    @PostMapping("/popular/topic")
    public BaseResponse<List<String>> getPopularTopic(HttpServletRequest request) throws IOException, ExecutionException, InterruptedException {
        // controller层对参数的校验

        List<String> popularTopic = searchService.getPopularTopic();
        return ResultUtils.success(popularTopic);
    }

    @GetMapping("/suggest")
    public BaseResponse<List<String>> searchSuggest(String suggestText) {
        // controller层对参数的校验
        System.out.println("suggest!!!!");

        List<String> suggestList = searchService.searchSuggestFromEs(suggestText);

        System.out.println(suggestList);
        return ResultUtils.success(suggestList);
    }

    @GetMapping("set/hot/words")
    public BaseResponse<List<Message>> setHotWords(String suggestTextStr, HttpServletRequest request) {
        // controller层对参数的校验
        if (StringUtils.isNullOrEmpty(suggestTextStr)) {
            return null;
        }

        List<Message> messageList = searchService.setHotWords(suggestTextStr, request);
        return ResultUtils.success(messageList);
    }

    @GetMapping("get/hot/words")
    public BaseResponse<List<MessageVO>> getHotWords() {
        // controller层对参数的校验

        List<MessageVO> hotWords = searchService.getHotWords();
        return ResultUtils.success(hotWords);
    }
}
