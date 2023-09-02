package com.yupi.springbootinit.model.vo;

import com.yupi.springbootinit.model.entity.Picture;
import com.yupi.springbootinit.model.entity.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 邓哈哈
 * 2023/8/31 16:31
 * Function:
 * Version 1.0
 */
@Data
public class SearchVO implements Serializable {
    private List<User> userList;

    private List<PostVO> postList;

    private List<Picture> pictureList;

    private List<?> dataList;

    private static final long serialVersionUID = 1L;
}
