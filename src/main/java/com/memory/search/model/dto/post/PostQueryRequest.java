package com.memory.search.model.dto.post;

import com.memory.search.common.PageRequest;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询请求
 *
 * @author <a href="https://gitee.com/deng-2022">回忆如初</a>
 * @from <a href="https://deng-2022.gitee.io/blog/">Memory's Blog</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PostQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 至少有一个标签
     */
    private List<String> orTags;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 诗人/词人
     */
    private String author;

    /**
     * 收藏用户 id
     */
    private Long favourUserId;

    private static final long serialVersionUID = 1L;
}