package com.yupi.springbootinit.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.Post;
import com.yupi.springbootinit.model.entity.User;

import javax.annotation.Resource;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 帖子收藏服务测试
 */
@SpringBootTest
class PostFavourServiceTest {
    @Resource
    private PostService postService;

    @Resource
    private PostFavourService postFavourService;

    private static final User loginUser = new User();

    @BeforeAll
    static void setUp() {
        loginUser.setId(1L);
    }

    @Test
    void doPostFavour() {
        int i = postFavourService.doPostFavour(1L, loginUser);
        Assertions.assertTrue(i >= 0);
    }

    @Test
    void listFavourPostByPage() {
        QueryWrapper<Post> postQueryWrapper = new QueryWrapper<>();
        postQueryWrapper.eq("id", 1L);
        postFavourService.listFavourPostByPage(Page.of(0, 1), postQueryWrapper, loginUser.getId());
    }

    @Test
    void timer() throws IOException {
//        String param = "动漫";
//        String encode = URLEncoder.encode(param, "utf-8");
//
//        String url = String.format("https://image.baidu.com/search/index?tn=baiduimage&word=%s", param);
//
//        Document document = Jsoup.connect(url).get();
//        System.out.println(document);

        int current = 1;
        String url = "http://www.netbian.com/";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 Edg/116.0.1938.62")
                .get();
        Elements elements = doc.select(".list li a");

        for (Element element : elements) {
            String title = element.attr("title");
            System.out.println(title);

            String imgUrl = element.select("img").attr("src");
            System.out.println(imgUrl);
        }
        System.out.println(elements);
    }

    @Test
    void testFetchPicture() throws IOException {
        int current = 1;
        String url = "https://cn.bing.com/images/search?q=water&first=" + current;
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictures = new ArrayList<>();
        for (Element element : elements) {
            // 取图片地址（murl）
            String m = element.select(".iusc").get(0).attr("m");
            Map<String, Object> map = JSONUtil.toBean(m, Map.class);
            String murl = (String) map.get("murl");
//            System.out.println(murl);
            // 取标题
            String title = element.select(".inflnk").get(0).attr("aria-label");
//            System.out.println(title);
            Picture picture = new Picture();
            picture.setTitle(title);
            picture.setUrl(murl);
            pictures.add(picture);
        }
        System.out.println(pictures);
    }

    @Test
    void testFetchPassage() {
        // 1. 获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url = "https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest
                .post(url)
                .body(json)
                .execute()
                .body();
//        System.out.println(result);
        // 2. json 转对象
        Map<String, Object> map = JSONUtil.toBean(result, Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records = (JSONArray) data.get("records");
        List<Post> articleList = new ArrayList<>();
        for (Object record : records) {
            JSONObject tempRecord = (JSONObject) record;
            Post post = new Post();
            post.setTitle(tempRecord.getStr("title"));
            post.setContent(tempRecord.getStr("content"));
            JSONArray tags = (JSONArray) tempRecord.get("tags");
            List<String> tagList = tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(1L);
            articleList.add(post);
        }
//        System.out.println(articleList);
        // 3. 数据入库
        boolean b = postService.saveBatch(articleList);
        Assertions.assertTrue(b);
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
        Elements heads = leftZhankai.select(".sons .cont div:nth-of-type(2)");

        ArrayList<Post> postList = new ArrayList<>();
        for (Element head : heads) {
            Post post = new Post();

            String title = head.select(">p:nth-of-type(1)").text();
            String author = head.select(">p:nth-of-type(2)").text();
            String content = head.select(".contson").text();
            System.out.println("------------------------------------");

            post.setTitle(title);
            post.setAuthor(author);
            post.setContent(content);
            postList.add(post);
        }

        boolean saveBatch = postService.saveBatch(postList);
        ThrowUtils.throwIf(!saveBatch, ErrorCode.OPERATION_ERROR, "批量插入诗词失败");


        // System.out.println(leftZhankai);
        // System.out.println(leftZhankai.text());
    }
}
