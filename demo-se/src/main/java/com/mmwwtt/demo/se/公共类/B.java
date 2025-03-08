package com.mmwwtt.demo.se.公共类;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
public class B extends A {

    public String message = "B";

    public static void showMessage() {
        log.info("B类的静态方法");
    }

    public String getMessage() {
        return "B";
    }

}
