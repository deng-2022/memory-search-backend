package com.yupi.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.springbootinit.constant.CommonConstant;
import com.yupi.springbootinit.mapper.ArticleMapper;
import com.yupi.springbootinit.model.dto.article.ArticleEsDTO;
import com.yupi.springbootinit.model.dto.article.ArticleQueryRequest;
import com.yupi.springbootinit.model.dto.post.PostEsHighlightData;
import com.yupi.springbootinit.model.entity.Article;
import com.yupi.springbootinit.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    @Override
    public Page<Article> searchFromEs(ArticleQueryRequest articleQueryRequest) {

        // 获取查询数据
        Long id = articleQueryRequest.getId();
        String searchText = articleQueryRequest.getSearchText();
        String title = articleQueryRequest.getTitle();
        String content = articleQueryRequest.getContent();
        List<String> tags = articleQueryRequest.getTags();
        Long userId = articleQueryRequest.getUserId();
        long pageNum = articleQueryRequest.getPageNum();
        long pageSize = articleQueryRequest.getPageSize();
        String sortField = articleQueryRequest.getSortField();
        String sortOrder = articleQueryRequest.getSortOrder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
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
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
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

        // 查询带highlight，标题和内容都带上
        HighlightBuilder highlightBuilder = new HighlightBuilder()
                .field("content")
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
        // 分页
        PageRequest pageRequest = PageRequest.of((int) pageNum, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder).build();
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

        Page<Article> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Article> resourceList = new ArrayList<>();

        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<ArticleEsDTO>> searchHitList = searchHits.getSearchHits();
            // 搜索关键词高亮
            Map<Long, PostEsHighlightData> highlightDataMap = new HashMap<>();
            for (SearchHit hit : searchHits.getSearchHits()) {
                PostEsHighlightData data = new PostEsHighlightData();
                data.setId(Long.valueOf(hit.getId()));
                if (hit.getHighlightFields().get("title") != null) {
                    String highlightTitle = String.valueOf(hit.getHighlightFields().get("title"));
                    data.setTitle(highlightTitle.substring(1, highlightTitle.length() - 1));
                    System.out.println(data.getTitle());
                }
                if (hit.getHighlightFields().get("content") != null) {
                    String highlightContent = String.valueOf(hit.getHighlightFields().get("content"));
                    data.setContent(highlightContent.substring(1, highlightContent.length() - 1));
                    System.out.println(data.getContent());
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
                        String hl_content = highlightDataMap.get(articleId).getContent();
                        if (hl_title != null && hl_title.trim() != "") {
                            article.setTitle(hl_title);
                        }
                        if (hl_content != null && hl_content.trim() != "") {
                            article.setContent(hl_content);
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




