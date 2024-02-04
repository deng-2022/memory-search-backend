package com.memory.search.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author <a href="https://gitee.com/deng-2022">回忆如初</a>
 * @from <a href="https://deng-2022.gitee.io/blog/">Memory's Blog</a>
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
