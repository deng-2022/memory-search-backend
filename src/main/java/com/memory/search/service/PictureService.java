package com.memory.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.entity.Picture;

import java.io.IOException;

/**
 * 图片爬取服务
 */
public interface PictureService {

    Page<Picture> listPictureVOByPage(String searchText, long pageSize, long currentPage) throws IOException;
}
