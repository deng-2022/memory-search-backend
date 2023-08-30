package com.yupi.springbootinit.model.dto.picture;

import lombok.Data;

/**
 * @author 邓哈哈
 * 2023/8/30 17:38
 * Function:
 * Version 1.0
 */
@Data
public class PictureQueryRequest {
    private String searchText;
    private int pageSize;
    private int currentPage;
}
