package com.mmwwtt.demo.ee.lombok;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class DemoTest {
    @Test
    public void test() {
        BaseInfoLombok baseInfoLombok1 = new BaseInfoLombok();
        baseInfoLombok1.getName();
        baseInfoLombok1.setName("欢欢");
        String str = baseInfoLombok1.toString();
        log.info(str);
    }
}
