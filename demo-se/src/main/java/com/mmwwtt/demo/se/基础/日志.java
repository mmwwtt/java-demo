package com.mmwwtt.demo.se.基础;

import java.util.logging.Logger;

public class 日志 {
    /**
     * 日志记录不要使用System.out/System.err在控制台打印，需要使用对应的日志框架(slf4j等)
     * 日志类需要声明为 private static final
     * private 防止实例对象被其他类非法使用
     * static 防止new出重复的实例对象，造成资源浪费
     * final 日志类无需做出任何改动
     */
    private static final Logger logger = null;
}
