package com.mmwwtt.demo.ee.lombok使用;

import org.junit.jupiter.api.Test;


public class LombokUserTest {
    @Test
    public void test() {
        LombokUser lombokUser1 = getLombokUser1();
        lombokUser1.getUserName();
        lombokUser1.setUserName("小明");
        String str = lombokUser1.toString();
    }

    public LombokUser getLombokUser1() {
        LombokUser lombokUser = new LombokUser();
        return lombokUser;
    }

    public LombokUser getLombokUser2() {
        LombokUser lombokUser = new LombokUser(Long.valueOf(1), "小明", 1,18);
        return lombokUser;
    }

    public LombokUser getLombokUser3() {
        LombokUser lombokUser = LombokUser.builder().userName("李华").age(1).build();
        return lombokUser;
    }
}
