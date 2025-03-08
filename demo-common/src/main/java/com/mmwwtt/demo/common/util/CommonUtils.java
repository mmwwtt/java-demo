package com.mmwwtt.demo.common.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

    public static void println(Object... list) {
        log.info("{}",list);
    }
}
