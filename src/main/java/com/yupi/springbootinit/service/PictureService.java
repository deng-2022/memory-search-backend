package com.yupi.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Picture;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * 图片爬取服务
 */
public interface PictureService {

    Page<Picture> listPictureVOByPage(String searchText, long pageSize, long currentPage) throws IOException;
}
