package com.mmwwtt.demo.ee.lombok使用;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;


@Slf4j
public class DemoTest {
    @Test
    public void test() {
        BaseInfoLombok baseInfoLombok1 = getLombokUser1();
        baseInfoLombok1.getName();
        baseInfoLombok1.setName("欢欢");
        String str = baseInfoLombok1.toString();
        log.info(str);
    }

    public BaseInfoLombok getLombokUser1() {
        BaseInfoLombok baseInfoLombok = new BaseInfoLombok();
        return baseInfoLombok;
    }

    public BaseInfoLombok getLombokUser3() {
        BaseInfoLombok baseInfoLombok = BaseInfoLombok.builder().name("欢欢").sex("0").build();
        return baseInfoLombok;
    }
}
