package com.memory.search.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.dto.picture.PictureQueryRequest;
import com.memory.search.model.entity.Picture;

import javax.servlet.http.HttpServletRequest;

/**
 * 图片爬取服务
 */
public interface PictureService {

    /**
     * 图片搜索
     *
     * @param searchText  搜索关键词
     * @param pageSize    每页容量
     * @param currentPage 当前页码
     * @return 图片列表
     */
    Page<Picture> listPictureVOByPage(String searchText, long pageSize, long currentPage);

    /**
     * 缓存图片管理
     *
     * @param pictureQueryRequest 图片获取参数
     * @param request             request
     * @return 缓存的图片
     */
    Page<Picture> listMyPostVOByPage(PictureQueryRequest pictureQueryRequest, HttpServletRequest request);
}
