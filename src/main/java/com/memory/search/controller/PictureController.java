package com.memory.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.common.BaseResponse;
import com.memory.search.common.ResultUtils;
import com.memory.search.model.dto.picture.PictureQueryRequest;
import com.memory.search.model.entity.Picture;
import com.memory.search.service.PictureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author 邓哈哈
 * 2023/8/30 17:05
 * Function:
 * Version 1.0
 */

@RestController
@RequestMapping("/picture")
@Slf4j
public class PictureController {
    @Resource
    private PictureService pictureService;


    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                  HttpServletRequest request) throws IOException {
        //controller层对参数的校验
        int currentPage = pictureQueryRequest.getCurrentPage();
        int pageSize = pictureQueryRequest.getPageSize();
        String searchText = pictureQueryRequest.getSearchText();

        Page<Picture> picturePage = pictureService.listPictureVOByPage(searchText, pageSize, currentPage);
        return ResultUtils.success(picturePage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param pictureQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/picture/vo")
    public BaseResponse<Page<Picture>> listMyPostVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                         HttpServletRequest request) {
        // 限制爬虫
        Page<Picture> listMyPostVOByPage = pictureService.listMyPostVOByPage(pictureQueryRequest, request);
        return ResultUtils.success(listMyPostVOByPage);
    }
}
