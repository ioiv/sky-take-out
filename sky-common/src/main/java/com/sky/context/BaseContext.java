package com.sky.context;

/**
 * 基于ThreadLocal的线程上下文工具类，用于存储和获取当前用户ID
 */
public class BaseContext {

    /**
     * ThreadLocal对象，用于存储当前用户ID
     * 每个线程都有其独立的用户ID副本，线程间数据隔离
     */
    public static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     * @param id 用户ID
     */
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    /**
     * 获取当前线程的用户ID
     * @return 当前用户ID
     */
    public static Long getCurrentId() {
        return threadLocal.get();
    }

    /**
     * 清理当前线程的用户ID
     * 用完后务必调用此方法，防止内存泄漏
     */
    public static void removeCurrentId() {
        threadLocal.remove();
    }

}
