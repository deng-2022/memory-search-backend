package com.memory.search.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.IOException;

/**
 * 适配器 目标值
 */
public interface DataSource<T> {
    /**
     * 统一查询
     *
     * @param searText 搜索词条
     * @param pageSize 每页数
     * @param current  当前页
     * @return Page
     */
    Page<T> search(String searText, long pageSize, long current) throws IOException;
}
