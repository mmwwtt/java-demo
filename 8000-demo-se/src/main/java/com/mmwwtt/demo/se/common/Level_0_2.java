package com.mmwwtt.demo.se.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Level_0_2 extends Level_0{
    public String message = "Level_0_2";

    public static void showMessage() {
        log.info("Level_0_2类的静态方法");
    }

    public String getMessage() {
        return "Level_0_2";
    }
}
