package com.memory.search.service.impl;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

public class SearchSuggestionExample {
    public static void main(String[] args) throws Exception {
        // 创建 Elasticsearch 客户端  
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http")));

        // 构建搜索请求  
        SearchRequest searchRequest = new SearchRequest("pinyin_index"); // 使用你的索引名称  
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery()); // 使用匹配所有查询作为示例查询  
        searchSourceBuilder.aggregation(AggregationBuilders.terms("top_tags").field("content.keyword").size(10)); // 使用 terms 聚合获取最常搜索的关键词  
        searchSourceBuilder.sort("content", SortOrder.ASC); // 可选：按内容字段升序排序结果  
        searchRequest.source(searchSourceBuilder);

        // 执行搜索请求并处理响应  
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        Terms topTags = searchResponse.getAggregations().get("top_tags");
        for (Terms.Bucket entry : topTags.getBuckets()) {
            String key = entry.getKeyAsString(); // 关键词  
            long docCount = entry.getDocCount(); // 文档数量  
            System.out.println("关键词: " + key + ", 文档数量: " + docCount);
        }

        // 关闭 Elasticsearch 客户端连接  
        client.close();
    }
}