package com.memory.search.job.cycle;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.search.model.entity.Article;
import com.memory.search.service.ArticleService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邓哈哈
 * 2023/12/22 21:55
 * Function:
 * Version 1.0
 */
// @Component
public class getArticleJob {
    @Resource
    private ArticleService articleService;

    List<String> contentIdList = new ArrayList<>();

    List<Article> articleList = new ArrayList<>();

    @Scheduled(cron = "*/2 * * * * *")
    public void getArticleContent() {
        getArticles();
        getContents();
    }

    void getArticles() {
        String url = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=6809637769959178254&type=hot&aid=2608&uuid=7202969973525005828&spider=0";
        HttpRequest request = HttpRequest.get(url);
        HttpResponse response = request.execute();
        String json = response.body();
        System.out.println(json);

        System.out.println("------------------------");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        JsonNode dataNode = rootNode.get("data");

        for (JsonNode jsonNode : dataNode) {
            JsonNode contentNode = jsonNode.get("content");
            String contentId = contentNode.get("content_id").asText();
            contentIdList.add(contentId);
            System.out.println("content_id: " + contentId);
        }

        System.out.println(contentIdList);
    }

    void getContents() {

        for (String contendId : contentIdList) {
            // 1. 获取数据
            String url = String.format("https://juejin.cn/post/%s", contendId);
            try {
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81")
                        .get();

                Elements title = doc.select(".article-area .article-title");
                System.out.println("----------------博文标题----------------");
                System.out.println(title);

                Element content = doc.getElementById("article-root");
                System.out.println("---------------博文正文------------------");

                System.out.println(content);

                // 获取博文
                Article article = new Article();
                article.setId(Long.valueOf(contendId));
                article.setTitle(title.html());
                // article.setContent(content != null ? content.html() : null);

                // 保存博文到列表
                boolean add = articleList.add(article);

                // 保存博文列表
                articleService.saveBatch(articleList);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
