package com.memory.search.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.memory.search.model.entity.Picture;
import com.memory.search.service.PictureService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author 邓哈哈
 * 2023/9/2 20:05
 * Function:
 * Version 1.0
 */

/**
 * 图片搜索 适配实现
 */
@Service
public class PictureDataSource implements DataSource<Picture> {
    @Resource
    private PictureService pictureService;

    @Override
    public Page<Picture> search(String searText, long pageSize, long current) {
        try {
            return pictureService.listPictureVOByPage(searText, pageSize, current);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
