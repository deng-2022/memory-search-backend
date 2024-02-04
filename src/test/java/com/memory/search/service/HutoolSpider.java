package com.memory.search.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.search.model.entity.Article;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 邓哈哈
 * 2023/12/21 23:22
 * Function:
 * Version 1.0
 */

@SpringBootTest
class HutoolSpider {

    @Resource
    private ArticleService articleService;

    /**
     * 存储博文 id
     */
    List<String> contentIdList = new ArrayList<>();


    @Test
    void getAll() {
        getArticles();
        getContents();
    }

    @Test
    void getArticles() {
        // 定义 URL
        String url = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=6809637769959178254&type=hot&aid=2608&uuid=7202969973525005828&spider=0";
        // 发起 HTTP GET 请求
        HttpRequest request = HttpRequest.get(url);
        // 获取响应结果
        HttpResponse response = request.execute();
        String json = response.body();
        System.out.println(json);

        System.out.println("------------------------");

        // 解析 JSON 字符串
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
    }

    @Test
    void getContents() {
        // for (String contendId : contentIdList) {
        //     // 1. 获取数据
        //     String url = String.format("https://juejin.cn/post/%s", contendId);
        //     try {
        //         Document doc = Jsoup.connect(url)
        //                 .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81")
        //                 .get();
        //
        //         // System.out.println(doc);
        //         Elements title = doc.select(".article-area .article-title");
        //         System.out.println("----------------博文标题----------------");
        //         System.out.println(title);
        //
        //         Element content = doc.getElementById("article-root");
        //         System.out.println("---------------博文正文------------------");
        //
        //         System.out.println(content);
        //
        //     } catch (IOException e) {
        //         throw new RuntimeException(e);
        //     }
        // }

        // 1. 获取数据
        String url = "https://juejin.cn/post/7313418992310976549";
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81")
                    .get();


            Elements title = doc.select(".article-area .article-title");
            System.out.println("----------------博文标题----------------");
            System.out.println(title.text());

            Elements content = doc.select(".article-viewer p");
            // for (Element p : content) {
            //     p.select("img").remove();
            //     p.select("a").remove();
            // }

            System.out.println("---------------博文正文------------------");
            // System.out.println(content);

            byte[] contentBytes = content.toString().getBytes(StandardCharsets.UTF_8);

            System.out.println(contentBytes);
            // 获取博文
            Article article = new Article();
            article.setId(Long.valueOf("7313418992310976777"));
            article.setTitle(title.text());
            article.setContent(contentBytes);
            // article.setContent(Arrays.toString(contentBytes));
            // article.setContent(content.toString());
            article.setAuthorId(0L);
            article.setView(0);
            article.setLikes(0);
            article.setComments("");
            article.setCollects(0);
            article.setTags("");

            articleService.save(article);

            String decodedContent = new String(contentBytes, StandardCharsets.UTF_8);
            System.out.println("-------------解码后--------------");
            System.out.println(decodedContent);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getArticleContentById() {
        Article article = articleService.getById(7313418992310976549L);

        Long id = article.getId();
        String title = article.getTitle();
        byte[] content = article.getContent();

        String decodedContent = new String(content, StandardCharsets.UTF_8);
        Integer type = article.getType();

        System.out.println(id);
        System.out.println(title);
        System.out.println(decodedContent);
        System.out.println(type);
    }

    @Test
    void getContents2() {
        // 1. 获取数据
        String url = "https://m.baidu.com/s?word=taiyuantianqi";
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81")
                    .get();

            System.out.println(doc);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //
    // Elements title = doc.select("title");
    // System.out.println("根据标签: " + title);
}
