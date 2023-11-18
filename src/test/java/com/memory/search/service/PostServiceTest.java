package com.memory.search.service;

import javax.annotation.Resource;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子服务测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class PostServiceTest {

    @Resource
    private PostService postService;

//    @Test
//    void searchFromEs() {
//        PostQueryRequest postQueryRequest = new PostQueryRequest();
//        postQueryRequest.setUserId(1L);
//        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
//        Assertions.assertNotNull(postPage);
//    }

}