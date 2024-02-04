package com.memory.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.memory.search.common.ErrorCode;
import com.memory.search.dataSource.*;
import com.memory.search.exception.ThrowUtils;
import com.memory.search.model.dto.article.ArticleEsDTO;
import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.entity.Message;
import com.memory.search.model.entity.Picture;
import com.memory.search.model.enums.SearchTypeEnum;
import com.memory.search.model.vo.ArticleVO;
import com.memory.search.model.vo.PostVO;
import com.memory.search.model.vo.SearchVO;
import com.memory.search.service.SearchService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    // 定义 Redis 键的模板
    private static final String REDIS_KEY_TEMPLATE = "search:hot:%s";
    // 定义 Redis 中存储的 Message 对象的字段
    private static final Long MESSAGE_ID = 18889893884L;
    private static final Integer SEARCH_NUM = 1;


    @Override
    public List<String> searchSuggestFromEs(String suggestText) {
        // 搜索建议
        SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion("suggestionTitle", new CompletionSuggestionBuilder("title.suggest").skipDuplicates(true).size(5).prefix(suggestText));

        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withSuggestBuilder(suggestBuilder)
                .build();
        // 执行搜索
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

        // 搜索建议词列表
        ArrayList<String> suggestionList = new ArrayList<>();

        // 从 SearchResponse 中获取建议
        Suggest suggest = searchHits.getSuggest();
        if (suggest != null) {
            // 获取特定的建议结果
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> termSuggestion = suggest.getSuggestion("suggestionTitle");
            // 遍历建议选项
            for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : termSuggestion.getEntries()) {
                // 处理每个建议选项，例如打印它们
                for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                    if (option != null) {
                        String text = option.getText();
                        suggestionList.add(text);
                    }
                }
            }
        }

        // 返回搜索建议词列表
        return suggestionList;
    }

    @Override
    public Message setHotWords(String suggestText, HttpServletRequest request) {
        // 获取当前登录用户
        // User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        //
        // Long userId = loginUser.getId();
        // ... 其他代码 ...

        // 从 Redis 中获取原始消息
        String messageStr = redisTemplate.opsForValue().get(String.format(REDIS_KEY_TEMPLATE, suggestText));

        Message message = null;
        Gson gson = new Gson();
        // 检查原始消息是否为空
        if (ObjectUtils.isNotEmpty(messageStr)) {
            // 获取原始消息中的 searchNum 字段并增加 1
            Message mes = gson.fromJson(messageStr, Message.class);
            Integer searchNum = mes.getSearchNum();
            Integer newSearchNum = searchNum + 1;

            // 创建新的 Message 对象
            message = new Message(MESSAGE_ID, suggestText, newSearchNum);
        } else {
            // 创建新的 Message 对象
            message = new Message(MESSAGE_ID, suggestText, SEARCH_NUM);
        }

        // 将新的 Message 对象存回 Redis 中，设置过期时间为 30 天
        redisTemplate.opsForValue()
                .set(String.format(REDIS_KEY_TEMPLATE, suggestText), gson.toJson(message), 30, TimeUnit.DAYS);

        return message;
    }

    @Override
    public List<Message> getHotWords() {
        Set<String> keys = redisTemplate.keys("search:hot*");

        ArrayList<Message> hotWordList = new ArrayList<>();
        Gson gson = new Gson();
        for (String key : Objects.requireNonNull(keys)) {
            String messageStr = redisTemplate.opsForValue().get(key);
            Message message = gson.fromJson(messageStr, Message.class);
            hotWordList.add(message);
        }

        System.out.println(keys);
        return hotWordList;
    }

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
            // 诗词搜索
            CompletableFuture<Page<PostVO>> postTask = CompletableFuture.supplyAsync(() ->
                    postDataSource.search(searchText, pageSize, current));

            // 博文搜索
            CompletableFuture<Page<ArticleVO>> articleTask = CompletableFuture.supplyAsync(() ->
                    articleDataSource.search(searchText, pageSize, current));

            // 图片搜索
            CompletableFuture<Page<Picture>> pictureTask = CompletableFuture.supplyAsync(() ->
                    pictureDataSource.search(searchText, pageSize, current));


            CompletableFuture.allOf(postTask, pictureTask, articleTask).join();

            try {
                Page<PostVO> postVOPage = postTask.get();
                Page<Picture> picturePage = pictureTask.get();
                Page<ArticleVO> articlePage = articleTask.get();

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
