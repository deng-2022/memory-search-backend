package com.memory.search.filter;

import com.alibaba.fastjson.JSON;
import com.memory.search.common.ErrorCode;
import com.memory.search.common.ResultUtils;
import com.memory.search.manager.BaseContext;
import com.memory.search.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.memory.search.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author 邓哈哈
 * 2023/1/13 10:19
 * Function:登录拦截
 * Version 1.0
 */

/**
 * 检查用户/员工是否完成登录
 */
// @WebFilter(filterName = "LoginCheckFilter")
@Component
@Slf4j
public class LoginCheckFilter implements Filter {// 验证是否登录
    // 路径匹配器,支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1.获取本次请求的URI
        String requestURI = request.getRequestURI();

        // 定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login", // 登录时
                "/employee/logout", // 登出时
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        // 2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);

        // 3.如果不需要处理,则直接放行
        if (check) {
            log.info("本次请求不需要处理...");
            filterChain.doFilter(request, response);// 放行请求
            return;
        }

        // 4.需要处理的用户的请求,则判断登录状态,如果已经登录,则直接放行
        Long userId;
        User currentUser = (User) request.getSession().getAttribute(USER_LOGIN_STATE);

        if (currentUser != null) {// Session中存储着用户id(登录成功)
            userId = currentUser.getId();
            log.info("该用户已登录, id为{}", userId);

            BaseContext.setCurrentId(userId);// ThreadLocal

            filterChain.doFilter(request, response);// 放行请求
            return;
        }

        // 5.如果未登录,则返回登录结果,通过输出流方式向客户端页面响应数据
        log.info("该用户未登录...");

        response.getWriter().write(JSON.toJSONString(ResultUtils.error(ErrorCode.NO_AUTH_ERROR)));
    }

    /**
     * 路径匹配,检查本次请求是否需要放行
     *
     * @param urls       :已定义的不需要处理的请求
     * @param requestURI :接收检查的请求
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {// 该请求不需要处理
                return true;
            }
        }
        return false;// 该请求得处理
    }
}
