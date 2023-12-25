package com.memory.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memory.search.constant.CommonConstant;
import com.memory.search.model.dto.article.ArticleEsDTO;
import com.memory.search.model.dto.article.ArticleEsHighlightData;
import com.memory.search.model.dto.article.ArticleQueryRequest;
import com.memory.search.mapper.ArticleMapper;
import com.memory.search.model.dto.article.ArticleDTO;
import com.memory.search.model.entity.Article;
import com.memory.search.model.vo.ArticleVO;
import com.memory.search.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
<<<<<<< HEAD
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
=======
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
<<<<<<< HEAD
import org.springframework.data.elasticsearch.core.suggest.response.Suggest;
=======
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
<<<<<<< HEAD
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
=======
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Lenovo
 */
@Service
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article>
        implements ArticleService {
    @Resource
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    /**
     * 获取文章信息
     *
     * @param articleDTO
     * @param request
     * @return
     */
    @Override
    public ArticleVO getArticle(ArticleDTO articleDTO, HttpServletRequest request) {
        Long id = articleDTO.getId();
        // 获取文章
        Article article = getById(id);
        // 封装文章和作者信息
        return getArticleVOByArticle(article);
    }

    /**
     * articleVO 为 articleVO
     *
     * @param article article
     * @return articleVO
     */
    public ArticleVO getArticleVOByArticle(Article article) {
        ArticleVO articleVO = new ArticleVO();

        articleVO.setId(article.getId());
        articleVO.setTitle(article.getTitle());
        articleVO.setDescription(article.getDescription());
<<<<<<< HEAD

        byte[] contentBytes = article.getContent();
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        articleVO.setContent(content);

=======
        articleVO.setContent(article.getContent());
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
        articleVO.setView(article.getView());
        articleVO.setLikes(article.getLikes());
        articleVO.setCollects(article.getCollects());
        articleVO.setComments(article.getComments());
        articleVO.setArticleUrl(article.getArticleUrl());
        articleVO.setTags(article.getTags());
        articleVO.setCreateTime(article.getCreateTime());
        articleVO.setUpdateTime(article.getUpdateTime());

        return articleVO;
    }

    @Override
    public Page<Article> searchFromEs(ArticleQueryRequest articleQueryRequest) {

        // 获取查询数据
        Long id = articleQueryRequest.getId();
        String searchText = articleQueryRequest.getSearchText();
        String title = articleQueryRequest.getTitle();
        String description = articleQueryRequest.getDescription();
        String content = articleQueryRequest.getContent();
        List<String> tags = articleQueryRequest.getTags();
        long pageNum = articleQueryRequest.getPageNum() - 1;
        long pageSize = articleQueryRequest.getPageSize();
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        // 必须包含所有标签
        if (CollectionUtils.isNotEmpty(tags)) {
            for (String tag : tags) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 按关键词检索 满足其一√
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(description)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", description));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
//         搜索关键词高亮
//        HighlightBuilder highlightBuilder = new HighlightBuilder();
//        highlightBuilder.field("*")
//                .preTags("<font color='#eea6b7'>")
//                .postTags("</font>"); //所有的字段都高亮
//        highlightBuilder.requireFieldMatch(false);//如果要多个字段高亮,这项要为false

        // 查询带highlight，标题和摘要都带上
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("description")
                .requireFieldMatch(false)
                .preTags("<font color='#eea6b7'>")
                .postTags("</font>");
        highlightBuilder.field("title")
                .requireFieldMatch(false)
                .preTags("<font color='#eea6b7'>")
                .postTags("</font>");

        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(CommonConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
<<<<<<< HEAD

        // 搜索建议
        SuggestBuilder suggestBuilder = new SuggestBuilder()
                .addSuggestion("suggestionTitle", new CompletionSuggestionBuilder("title.suggest").skipDuplicates(true).size(5).prefix(searchText));

=======
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
        // 分页
        PageRequest pageRequest = PageRequest.of((int) pageNum, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageRequest)
<<<<<<< HEAD
                .withSorts(sortBuilder)
                .withSuggestBuilder(suggestBuilder)
                .build();
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);


        // 从 SearchResponse 中获取建议
        Suggest suggest = searchHits.getSuggest();
        if (suggest != null) {
            // 获取特定的建议结果
            Suggest.Suggestion<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> termSuggestion = suggest.getSuggestion("suggestionTitle");
            if (termSuggestion != null) {
                // 遍历建议选项
                for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : termSuggestion.getEntries()) {
                    // 处理每个建议选项，例如打印它们
                    System.out.println(entry.getText());
                    System.out.println(entry.getOptions().get(0).getText());
                    System.out.println(entry.getOptions().get(1).getText());
                }
            }
        }

=======
                .withSorts(sortBuilder).build();
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
        Page<Article> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Article> resourceList = new ArrayList<>();

        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<ArticleEsDTO>> searchHitList = searchHits.getSearchHits();
            // 搜索关键词高亮
            Map<Long, ArticleEsHighlightData> highlightDataMap = new HashMap<>();
            for (SearchHit hit : searchHits.getSearchHits()) {
                ArticleEsHighlightData data = new ArticleEsHighlightData();
                data.setId(Long.valueOf(hit.getId()));
                if (hit.getHighlightFields().get("title") != null) {
                    String highlightTitle = String.valueOf(hit.getHighlightFields().get("title"));
                    data.setTitle(highlightTitle.substring(1, highlightTitle.length() - 1));
<<<<<<< HEAD
                    // System.out.println(data.getTitle());
=======
                    System.out.println(data.getTitle());
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
                }
                if (hit.getHighlightFields().get("description") != null) {
                    String highlightContent = String.valueOf(hit.getHighlightFields().get("description"));
                    data.setDescription(highlightContent.substring(1, highlightContent.length() - 1));
<<<<<<< HEAD
                    // System.out.println(data.getContent());
=======
                    System.out.println(data.getContent());
>>>>>>> 1865428977bda5d1cd80d3ff86f30f41e0e5add8
                }
                highlightDataMap.put(data.getId(), data);
            }

            // id列表
            List<Long> articleIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            // 根据id查找数据集
            List<Article> articleList = baseMapper.selectBatchIds(articleIdList);
            if (articleList != null) {
                Map<Long, List<Article>> idArticleMap = articleList.stream().collect(Collectors.groupingBy(Article::getId));
                articleIdList.forEach(articleId -> {
                    if (idArticleMap.containsKey(articleId)) {
                        // 搜索关键词高亮替换
                        Article article = idArticleMap.get(articleId).get(0);
                        String hl_title = highlightDataMap.get(articleId).getTitle();
                        String hl_des = highlightDataMap.get(articleId).getDescription();
                        if (hl_title != null && hl_title.trim() != "") {
                            article.setTitle(hl_title);
                        }
                        if (hl_des != null && hl_des.trim() != "") {
                            article.setDescription(hl_des);
                        }
                        resourceList.add(article);
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(articleId), ArticleEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }

        page.setRecords(resourceList);
        return page;
    }
}




