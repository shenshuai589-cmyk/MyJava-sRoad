package com.campus.secondhand.utils;

public class UserContext {

    private static final ThreadLocal<Long> USER_HOLDER = new ThreadLocal<>();


    /**
     * 保存用户id
     */
    public static void setUserId(Long userId) {
        USER_HOLDER.set(userId);
    }
    /**
     * 获取用户id
     */

    public static Long getUserId() {
        return USER_HOLDER.get();
    }

    /**
     * 清楚
     */

    public static void remove() {
        USER_HOLDER.remove();
    }
}
