package com.memory.search.manager.job.cycle;

import cn.hutool.json.JSONUtil;
import com.memory.search.model.entity.Picture;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 邓哈哈
 * 2023/12/4 23:38
 * Function:
 * Version 1.0
 */

// @Component
@EnableScheduling
public class getPicJob {
    @Scheduled(cron = "*/2 * * * * *")
    public void getArticleTest() {
        // 请求URL
        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", "flower", 1);
        Document doc = null;
        // 响应内容
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 解析元素
        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();
        int count = 0;
        for (Element element : elements) {
            String mUrl = getImageUrl(element);
            String title = getTitle(element);
            Picture picture = new Picture(title, mUrl);
            pictureList.add(picture);
            count++;
            if (count > 15) {
                break;
            }
        }
        System.out.println("获取的Pic列表: " + pictureList);
    }

    private String getImageUrl(Element element) {
        String m = element.select(".iusc").get(0).attr("m");
        Map<String, Object> map = JSONUtil.toBean(m, Map.class);
        return (String) map.get("murl");
    }

    private String getTitle(Element element) {
        return element.select(".inflnk").get(0).attr("aria-label");
    }
}
