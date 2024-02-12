package com.memory.search.mapper;

import com.memory.search.model.entity.Post;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 帖子数据库操作测试
 *
 * @author <a href="https://gitee.com/deng-2022">回忆如初</a>
 * @from <a href="https://deng-2022.gitee.io/blog/">Memory's Blog</a>
 */
@SpringBootTest
class PostMapperTest {

    @Resource
    private PostMapper postMapper;

    @Test
    void listPostWithDelete() {
        List<Post> articleList = postMapper.listPostWithDelete(new Date());
        Assertions.assertNotNull(articleList);
    }
}