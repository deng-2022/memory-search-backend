package com.memory.search.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.dto.post.PostQueryRequest;
import com.memory.search.model.entity.Post;
import com.memory.search.model.vo.PostVO;
import com.memory.search.service.PostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 邓哈哈
 * 2023/9/2 20:05
 * Function:
 * Version 1.0
 */

/**
 * 文章搜索 适配实现
 */
@Service
public class PostDataSource implements DataSource<PostVO> {
    @Resource
    private PostService postService;

    @Override
    public Page<PostVO> search(String searText, long pageSize, long current) {
        PostQueryRequest postQueryRequest = new PostQueryRequest();

        postQueryRequest.setSearchText(searText);
        postQueryRequest.setPageSize(pageSize);
        postQueryRequest.setPageNum(current);

        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return postService.getPostVOPage(postPage, null);
    }
}

