package com.yupi.springbootinit.dataSource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.springbootinit.model.dto.user.UserQueryRequest;
import com.yupi.springbootinit.model.entity.User;
import com.yupi.springbootinit.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 邓哈哈
 * 2023/9/2 20:05
 * Function:
 * Version 1.0
 */

/**
 * 用户搜索 适配实现
 */
@Service
public class UserDataSource implements DataSource<User> {
    @Resource
    private UserService userService;

    @Override
    public Page<User> search(String searText, long pageSize, long current) {
        UserQueryRequest userQueryRequest = new UserQueryRequest();

        userQueryRequest.setUserName(searText);
        userQueryRequest.setPageSize(pageSize);
        userQueryRequest.setCurrent(current);

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        return userService.listUserVOByPage(userQueryRequest, request);
    }
}
