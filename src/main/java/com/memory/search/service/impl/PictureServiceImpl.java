package com.memory.search.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.entity.Picture;
import com.memory.search.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author 邓哈哈
 * 2023/8/30 17:23
 * Function:
 * Version 1.0
 */
@Service
public class PictureServiceImpl implements PictureService {

    /**
     * 爬取图片
     *
     * @param searchText  参数
     * @param pageSize    数量
     * @param currentPage 当前页
     */
    @Override
    public Page<Picture> listPictureVOByPage(String searchText, long pageSize, long currentPage) throws IOException {
        long current = currentPage - 1;
        // 非空条件，转码
        if (StringUtils.isNotBlank(searchText)) {
            searchText = URLEncoder.encode(searchText, "UTF-8");
        }

        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = Jsoup.connect(url).get();
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

        Page<Picture> picturePage = new Page<>(pageSize, currentPage);

        picturePage.setRecords(pictureList);

        return picturePage;
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
