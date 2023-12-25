package com.memory.search.service;

import com.memory.search.model.entity.Post;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;

/**
 * 帖子服务测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

    //    @Test
//    void searchFromEs() {
//        PostQueryRequest postQueryRequest = new PostQueryRequest();
//        postQueryRequest.setUserId(1L);
//        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//        Assertions.assertNotNull(postPage);
//    }
    @Test
    public void getArticle() {

    }

    @Test
    void testFetch() throws IOException {
        // 1. 获取数据
        String url = "https://so.gushiwen.cn/shiwens/default.aspx?page=6&astr=%E6%9D%9C%E7%94%AB";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.62")
                .get();
        // 输出整个 HTML 文档
        // System.out.println(doc);

        // 捕获 class = "titletype"
        Elements elements = doc.select(".titletype");
        // System.out.println(elements);
        for (Element element : elements) {
            System.out.println(element);
            System.out.println(element.text());
            System.out.println("------------");
        }

        // 捕获 id = "leftZhankai"
        Element leftZhankai = doc.getElementById("leftZhankai");
        System.out.println(leftZhankai);
        System.out.println(leftZhankai.text());
    }

    @Test
    void testFetchPoem() throws IOException {
        // 1. 获取数据
        String url = "https://so.gushiwen.cn/shiwens/default.aspx?page=6&astr=%E6%9D%9C%E7%94%AB";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.62")
                .get();
        // 输出整个 HTML 文档
        // System.out.println(doc);

        // 捕获 class = "titletype"
        // Elements elements = doc.select(".titletype");
        // for (Element element : elements) {
        //     System.out.println(element);
        //     System.out.println(element.text());
        // }

        // 捕获 id = "leftZhankai"
        Element leftZhankai = doc.getElementById("leftZhankai");
        System.out.println(leftZhankai);
        System.out.println("----------------------------------------------");
        Elements heads = leftZhankai.select(".sons .cont div:nth-of-type(2)");
        System.out.println(heads);

        ArrayList<Post> postList = new ArrayList<>();
        for (Element head : heads) {
            Post post = new Post();

            String title = head.select(">p:nth-of-type(1)").text();
            String author = head.select(">p:nth-of-type(2)").text();
            String content = head.select(".contson").text();
            System.out.println(title);
            System.out.println(author);
            System.out.println(content);

            System.out.println("------------------------------------");

            post.setTitle(title);
            post.setAuthor(author);
            post.setContent(content);
            postList.add(post);
        }

        // boolean saveBatch = postService.saveBatch(postList);
        // ThrowUtils.throwIf(!saveBatch, ErrorCode.OPERATION_ERROR, "批量插入诗词失败");


        // System.out.println(leftZhankai);
        // System.out.println(leftZhankai.text());
    }

    @Test
    void testFetchArticle() throws IOException {
        // 1. 获取数据
        String url = "https://api.juejin.cn/content_api/v1/content/article_rank?category_id=6809637769959178254&type=hot&aid=2608&uuid=7202969973525005828&spider=0";
        String params = "?category_id=6809637769959178254&type=hot&aid=2608&uuid=7202969973525005828&spider=0";
        Document doc = Jsoup.connect(url + params)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.81")
                .get();
        // 添加参数
        // 输出整个 HTML 文档
        System.out.println(doc.text());
        System.out.println(doc);
        // 捕获 id = "leftZhankai"
        // Element app = doc.getElementById("app");
        // System.out.println(app);

        // 捕获 class = "hot-list"
        // Elements elements = doc.select(".view-container .main-container .hot-list-body .hot-list-wrap .hot-list");
        // System.out.println(elements);
        // System.out.println(elements.text());
        // for (Element element : elements) {
        //     System.out.println(element.text());
        // }

        // 捕获 id = "leftZhankai"
        // Element leftZhankai = doc.getElementById("leftZhankai");
        // System.out.println(leftZhankai);
        // System.out.println("----------------------------------------------");
        // Elements heads = leftZhankai.select(".sons .cont div:nth-of-type(2)");
        // System.out.println(heads);
        //
        // ArrayList<Post> postList = new ArrayList<>();
        // for (Element head : heads) {
        //     Post post = new Post();
        //
        //     String title = head.select(">p:nth-of-type(1)").text();
        //     String author = head.select(">p:nth-of-type(2)").text();
        //     String content = head.select(".contson").text();
        //     System.out.println(title);
        //     System.out.println(author);
        //     System.out.println(content);
        //
        //     System.out.println("------------------------------------");
        //
        //     post.setTitle(title);
        //     post.setAuthor(author);
        //     post.setContent(content);
        //     postList.add(post);
        // }

        // boolean saveBatch = postService.saveBatch(postList);
        // ThrowUtils.throwIf(!saveBatch, ErrorCode.OPERATION_ERROR, "批量插入诗词失败");


        // System.out.println(leftZhankai);
        // System.out.println(leftZhankai.text());
    }


}