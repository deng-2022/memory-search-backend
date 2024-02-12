package com.memory.search.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.memory.search.common.BaseResponse;
import com.memory.search.common.ErrorCode;
import com.memory.search.common.ResultUtils;
import com.memory.search.exception.BusinessException;
import com.memory.search.exception.ThrowUtils;
import com.memory.search.model.dto.article.ArticleDTO;
import com.memory.search.model.dto.article.ArticleQueryRequest;
import com.memory.search.model.dto.post.PostAddRequest;
import com.memory.search.model.entity.Article;
import com.memory.search.model.vo.ArticleVO;
import com.memory.search.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    private final static Gson GSON = new Gson();

    /**
     * 创建
     *
     * @param postAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Article article = new Article();
        BeanUtils.copyProperties(postAddRequest, article);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            article.setTags(GSON.toJson(tags));
        }

        boolean result = articleService.save(article);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = article.getId();
        return ResultUtils.success(newPostId);
    }

    /**
     * 分页获取诗词（封装类）
     *
     * @param articleQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Article>> listPostVOByPage(@RequestBody ArticleQueryRequest articleQueryRequest,
                                                     HttpServletRequest request) {
        long pageNum = articleQueryRequest.getPageNum();
        long size = articleQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        Page<Article> ArticlePage = articleService.page(new Page<>(pageNum, size));

        // Page<PostVO> postVOPage = articleService.listPostVOByPage(postQueryRequest, request);
        return ResultUtils.success(ArticlePage);
    }


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
