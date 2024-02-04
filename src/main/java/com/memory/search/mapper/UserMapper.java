package com.memory.search.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.memory.search.model.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户数据库操作
 *
 * @author <a href="https://gitee.com/deng-2022">回忆如初</a>
 * @from <a href="https://deng-2022.gitee.io/blog/">Memory's Blog</a>
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




