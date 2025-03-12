package com.mmwwtt.demo.se.面向对象;

import com.mmwwtt.demo.se.公共类.A;
import org.junit.jupiter.api.Test;

public class 调用静态方法 {

    /**
     * 要使用类名来调用静态方法，不要同时实例来调用
     */
    @Test
    public void 调用静态方法示例() {
        A.showMessage();
    }
}
