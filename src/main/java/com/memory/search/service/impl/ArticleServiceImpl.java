package com.memory.search.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.memory.search.constant.CommonConstant;
import com.memory.search.manager.BaseContext;
import com.memory.search.mapper.ArticleMapper;
import com.memory.search.model.dto.article.ArticleDTO;
import com.memory.search.model.dto.article.ArticleEsDTO;
import com.memory.search.model.dto.article.ArticleEsHighlightData;
import com.memory.search.model.dto.article.ArticleQueryRequest;
import com.memory.search.model.entity.Article;
import com.memory.search.model.entity.User;
import com.memory.search.model.vo.ArticleVO;
import com.memory.search.model.vo.UserVO;
import com.memory.search.service.ArticleService;
import com.memory.search.service.UserService;
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
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
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

    @Resource
    private UserService userService;


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

        byte[] contentBytes = article.getContent();
        String content = new String(contentBytes, StandardCharsets.UTF_8);
        articleVO.setContent(content);

        articleVO.setView(article.getView());
        articleVO.setLikes(article.getLikes());
        articleVO.setCollects(article.getCollects());
        articleVO.setComments(article.getComments());
        articleVO.setArticleUrl(article.getArticleUrl());
        articleVO.setTags(article.getTags());
        articleVO.setCreateTime(article.getCreateTime());
        articleVO.setUpdateTime(article.getUpdateTime());

        Long currentId = BaseContext.getCurrentId();
        User user = userService.getById(currentId);
        UserVO userVO = userService.getUserVO(user);
        articleVO.setUserVO(userVO);

        return articleVO;
    }

    @Override
    public Page<ArticleVO> searchFromEs(ArticleQueryRequest articleQueryRequest) {

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

        // 配置支持搜索高亮的字段和高亮关键词样式
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

        // 分页
        PageRequest pageRequest = PageRequest.of((int) pageNum, (int) pageSize);

        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withHighlightBuilder(highlightBuilder)
                .withPageable(pageRequest)
                .withSorts(sortBuilder)
                .build();

        // 执行搜索，获取搜索结果
        SearchHits<ArticleEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, ArticleEsDTO.class);

        Page<ArticleVO> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());

        List<ArticleVO> resourceList = new ArrayList<>();
        // 组织高亮关键词搜索结果
        if (searchHits.hasSearchHits()) {
            // 依次获取所有高亮关键词
            Map<Long, ArticleEsHighlightData> highlightDataMap = new HashMap<>();
            for (SearchHit hit : searchHits.getSearchHits()) {
                // 保存高亮关键词
                ArticleEsHighlightData data = new ArticleEsHighlightData();
                data.setId(Long.valueOf(hit.getId()));

                if (hit.getHighlightFields().get("title") != null) {
                    String highlightTitle = String.valueOf(hit.getHighlightFields().get("title"));
                    // 记录文档中 title 字段的高亮关键词
                    data.setTitle(highlightTitle.substring(1, highlightTitle.length() - 1));
                    System.out.println(data.getTitle());
                }
                if (hit.getHighlightFields().get("description") != null) {
                    String highlightContent = String.valueOf(hit.getHighlightFields().get("description"));
                    // 记录文档中 content 字段的高亮关键词
                    data.setDescription(highlightContent.substring(1, highlightContent.length() - 1));
                    System.out.println(data.getContent());
                }

                // 保存所有高亮关键词
                highlightDataMap.put(data.getId(), data);
            }

            // 保存搜索结果的 id 列表
            List<SearchHit<ArticleEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> articleIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());

            // 根据 id 在 MySQL 数据库中查询完整记录
            List<Article> articleList = baseMapper.selectBatchIds(articleIdList);

            // 使用高亮关键词替换原文本
            if (articleList != null) {
                Map<Long, List<Article>> idArticleMap = articleList.stream().collect(Collectors.groupingBy(Article::getId));
                articleIdList.forEach(articleId -> {
                    if (idArticleMap.containsKey(articleId)) {
                        // 拿到高亮关键词
                        Article article = idArticleMap.get(articleId).get(0);
                        String hl_title = highlightDataMap.get(articleId).getTitle();
                        String hl_des = highlightDataMap.get(articleId).getDescription();

                        // 高亮关键词替换原文本
                        if (hl_title != null && hl_title.trim() != "") {
                            article.setTitle(hl_title);
                        }
                        if (hl_des != null && hl_des.trim() != "") {
                            article.setDescription(hl_des);
                        }

                        ArticleVO articleVO = getArticleVOByArticle(article);

                        resourceList.add(articleVO);
                    } else {
                        // MySQL 已物理删除该 id 对应记录，同步从 ES 清空数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(articleId), ArticleEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }

        // 返回最终搜索结果到前端
        page.setRecords(resourceList);
        return page;
    }
}




