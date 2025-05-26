package com.mmwwtt.demo.se.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Level_0 {

    public String message = "Level_0";

    public static void showMessage() {
        log.info("Level_0类的静态方法");
    }
    public String getMessage() {
        return "Level_0";
    }

}
