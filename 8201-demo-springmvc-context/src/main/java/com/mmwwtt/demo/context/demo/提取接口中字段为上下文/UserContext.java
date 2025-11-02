package com.mmwwtt.demo.context.demo.提取接口中字段为上下文;

import lombok.Data;

@Data
public class UserContext {
    private static final ThreadLocal<Long> baseInfoId = new ThreadLocal<>();

    private UserContext() {}

    /* ==== 写 ==== */
    public static void setBaseInfoId(Long val) { baseInfoId.set(val); }

    /* ==== 读 ==== */
    public static Long getBaseInfoId() { return baseInfoId.get(); }

    /* ==== 清理 ==== */
    public static void clear() {
        baseInfoId.remove();
    }
}
