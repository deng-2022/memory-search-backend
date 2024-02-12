package com.memory.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.memory.search.common.ErrorCode;
import com.memory.search.dataSource.*;
import com.memory.search.exception.ThrowUtils;
import com.memory.search.model.dto.article.ArticleEsDTO;
import com.memory.search.model.dto.message.MessageTest;
import com.memory.search.model.dto.search.SearchQueryRequest;
import com.memory.search.model.entity.Message;
import com.memory.search.model.entity.Picture;
import com.memory.search.model.enums.SearchTypeEnum;
import com.memory.search.model.vo.ArticleVO;
import com.memory.search.model.vo.MessageVO;
import com.memory.search.model.vo.PostVO;
import com.memory.search.model.vo.SearchVO;
import com.memory.search.service.SearchService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.core.ElasticsearchAggregations;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
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
    private static final String SEARCH_TEXT_KEY = "search:text:%s";
    private static final String SEARCH_TIME = "2024/02";

    // 定义 Redis 中存储的 Message 对象的字段
    private static final Long MESSAGE_ID = 18889893884L;
    private static final Double SEARCH_NUM = 1.0;


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
    public List<String> getPopularTopic() {

        // 创建BoolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 添加聚合条件
        TermsAggregationBuilder search_terms = AggregationBuilders.terms("search_terms").field("message").size(10);

        // 创建查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withAggregations(search_terms)   // 添加聚合条件
                .build();

        // 执行搜索，获取搜索结果
        SearchHits<MessageTest> searchHits = elasticsearchRestTemplate.search(searchQuery, MessageTest.class);

        // 获取Spring Data Elasticsearch的聚合结果
        ElasticsearchAggregations searchHitsAggregations = (ElasticsearchAggregations) searchHits.getAggregations();
        Aggregations aggregations = Objects.requireNonNull(searchHitsAggregations).aggregations();
        List<Aggregation> aggregationList = aggregations.asList();
        for (Aggregation aggregation : aggregationList) {
            System.out.println(aggregation);
            aggregation.getName();
            // 检查聚合类型并转换为相应的聚合对象
            if (aggregation instanceof Terms) {
                Terms termsAggregation = (Terms) aggregation;

                // 获取buckets
                List<? extends Terms.Bucket> buckets = termsAggregation.getBuckets();

                // 遍历buckets
                for (Terms.Bucket bucket : buckets) {
                    System.out.println("Bucket Key: " + bucket.getKeyAsString());
                    System.out.println("Bucket Doc Count: " + bucket.getDocCount());
                    // 如果需要，还可以获取其他bucket信息，如聚合的子聚合等
                }
            }
        }



        List<SearchHit<MessageTest>> searchHitsSearchHits = searchHits.getSearchHits();
        System.out.println(searchHitsSearchHits);

        System.out.println(// 获取聚合结果
                Objects.requireNonNull(searchHits.getAggregations()).aggregations());
        System.out.println("--------------------------------");
        System.out.println();

        // 获取聚合结果



        return null;
    }

    @Override
    public List<Message> setHotWords(String searchTextStr, HttpServletRequest request) {
        // 获取当前登录用户
        // User loginUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        //
        // Long userId = loginUser.getId();

        if (com.qcloud.cos.utils.StringUtils.isNullOrEmpty(searchTextStr)) {
            return null;
        }

        // 按空格，获取每个搜索词
        String[] searchTexts = searchTextStr.split(" ");

        ArrayList<Message> messageList = new ArrayList<>();
        for (String searchText : searchTexts) {
            // 从 Redis 中获取原始消息
            Long size = redisTemplate.opsForZSet()
                    .zCard(String.format(SEARCH_TEXT_KEY, SEARCH_TIME));

            Message message = null;
            Gson gson = new Gson();
            // 原始消息不为空
            if (size > 0) {
                // 创建新的 Message 对象
                message = new Message(MESSAGE_ID, searchText);
                // 存入 Redis，更新 score
                redisTemplate.opsForZSet()
                        .incrementScore(String.format(SEARCH_TEXT_KEY, SEARCH_TIME), gson.toJson(message), 1);

            } else {
                // 创建新的 Message 对象
                message = new Message(MESSAGE_ID, searchText);

                // 将新的 Message 对象存回 Redis 中
                redisTemplate.opsForZSet()
                        .add(String.format(SEARCH_TEXT_KEY, SEARCH_TIME), gson.toJson(message), SEARCH_NUM);
            }
            boolean add = messageList.add(message);
            ThrowUtils.throwIf(!add, ErrorCode.OPERATION_ERROR, "记录热搜词失败");
        }

        return messageList;
    }

    @Override
    public List<MessageVO> getHotWords() {
        // 根据 score 获取前十条搜索词条
        Set<String> messageSet = redisTemplate.opsForZSet()
                .reverseRange(String.format(SEARCH_TEXT_KEY, SEARCH_TIME), 0, 9);

        ArrayList<MessageVO> messageVOList = new ArrayList<>();
        Gson gson = new Gson();
        for (String messageStr : Objects.requireNonNull(messageSet)) {
            Message message = gson.fromJson(messageStr, Message.class);
            // 查询搜索词条对应 score
            Double score = redisTemplate.opsForZSet()
                    .score(String.format(SEARCH_TEXT_KEY, SEARCH_TIME), messageStr);
            // 封装 messageVO
            MessageVO messageVO = new MessageVO();
            BeanUtils.copyProperties(message, messageVO);
            messageVO.setSearchNum(score);

            boolean add = messageVOList.add(messageVO);
            ThrowUtils.throwIf(!add, ErrorCode.OPERATION_ERROR, "获取热搜词失败");
        }

        return messageVOList;
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
                // searchVO.setPostVOList(postVOPage.getRecords());
                // searchVO.setPictureList(picturePage.getRecords());
                // searchVO.setArticleList(articlePage.getRecords());

                searchVO.setPostVOPage(postVOPage);
                searchVO.setArticleVOPage(articlePage);
                searchVO.setPicturePage(picturePage);

            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        } else {
            // 分类查询
            searchVO = new SearchVO();
            DataSource<?> dataSourceByType = dataSourceRegistry.getDataSourceByType(type);
            try {
                Page<?> page = dataSourceByType.search(searchText, pageSize, current);
                searchVO.setDataPage(page);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return searchVO;
    }
}
