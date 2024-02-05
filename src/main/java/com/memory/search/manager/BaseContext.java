package com.memory.search.manager;

/**
 * @author 邓哈哈
 * 2023/1/16 11:18
 * Function:
 * Version 1.0
 */

/**
 * 基于ThreadLocal封装工具类,用户保存和获取当前登录用户id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal();

    /**
     * 设置当前线程的局部变量的值
     *
     * @param id
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 返回当前线程对应的局部变量的值
     *
     * @return
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
