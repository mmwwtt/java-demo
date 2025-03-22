package com.mmwwtt.demo.se.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class A {

    public String message = "A";

    public static void showMessage() {
        log.info("A类的静态方法");
    }
    public String getMessage() {
        return "A";
    }

}
