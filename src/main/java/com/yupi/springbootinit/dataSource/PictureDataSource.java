package com.yupi.springbootinit.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.vo.PostVO;
import com.yupi.springbootinit.service.PictureService;
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
