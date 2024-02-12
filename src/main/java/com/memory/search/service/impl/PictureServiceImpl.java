package com.memory.search.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.memory.search.constant.CommonConstant;
import com.memory.search.manager.BaseContext;
import com.memory.search.model.dto.picture.PictureQueryRequest;
import com.memory.search.model.entity.Picture;
import com.memory.search.service.PictureService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 邓哈哈
 * 2023/8/30 17:23
 * Function:
 * Version 1.0
 */
@Service
public class PictureServiceImpl implements PictureService {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    private static final String SEARCH_PICTURE_KEY = "picture:url:%s";

    private final static Gson GSON = new Gson();

    /**
     * 图片搜索
     *
     * @param searchText  搜索关键词
     * @param pageSize    每页容量
     * @param currentPage 当前页码
     * @return 图片列表
     */
    @Override
    public Page<Picture> listPictureVOByPage(String searchText, long pageSize, long currentPage) {
        long current = currentPage - 1;

        if (searchText == null) {
            return null;
        }

        // 非空条件，转码
        if (StringUtils.isNotBlank(searchText)) {
            try {
                searchText = URLEncoder.encode(searchText, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        String url = String.format("https://cn.bing.com/images/search?q=%s&first=%s", searchText, current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Elements elements = doc.select(".iuscp.isv");
        List<Picture> pictureList = new ArrayList<>();

        Long currentId = BaseContext.getCurrentId();
        int count = 0;
        for (Element element : elements) {
            String mUrl = getImageUrl(element);
            String title = getTitle(element);
            Picture picture = new Picture(title, mUrl);

            // 图片列表
            pictureList.add(picture);
            count++;
            if (count > CommonConstant.PICTURE_NUMS) {
                break;
            }
        }

        // Redis 保存搜索图片 两小时
        redisTemplate.opsForValue()
                .set(String.format(SEARCH_PICTURE_KEY, String.valueOf(currentId)),
                        GSON.toJson(pictureList), 2, TimeUnit.HOURS);

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

    /**
     * 缓存图片管理
     *
     * @param pictureQueryRequest 图片获取参数
     * @param request             request
     * @return 缓存的图片
     */
    @Override
    public Page<Picture> listMyPostVOByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request) {
        // 获取缓存的图片
        Long currentId = BaseContext.getCurrentId();
        String pictureListStr = redisTemplate.opsForValue().get(String.format(SEARCH_PICTURE_KEY, String.valueOf(currentId)));
        List<Picture> pictureList = GSON.fromJson(pictureListStr, new TypeToken<List<Picture>>() {
        }.getType());
        Page<Picture> picturePage = new Page<>();

        picturePage.setRecords(pictureList);
        return picturePage;
    }
}
