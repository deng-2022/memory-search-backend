package com.yupi.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ResultUtils;
import com.yupi.springbootinit.model.dto.article.ArticleDTO;
import com.yupi.springbootinit.model.vo.ArticleVO;
import com.yupi.springbootinit.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 邓哈哈
 * 2023/6/9 23:24
 * Function:
 * Version 1.0
 */

@RestController
@RequestMapping("/article")
public class ArticleController {
    @Resource
    private ArticleService articleService;

    /**
     * 获取博文
     *
     * @param articleDTO
     * @param request
     * @return
     */
    @GetMapping("/get/VO")
    public BaseResponse<ArticleVO> getArticleById(ArticleDTO articleDTO, HttpServletRequest request) {
        // controller对参数的校验

        ArticleVO articleVO = articleService.getArticle(articleDTO, request);
        return ResultUtils.success(articleVO);
    }
}
