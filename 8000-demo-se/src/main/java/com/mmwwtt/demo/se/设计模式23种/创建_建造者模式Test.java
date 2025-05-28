package com.mmwwtt.demo.se.设计模式23种;

import com.mmwwtt.demo.common.entity.BaseInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
public class 创建_建造者模式Test {

    @Test
    @DisplayName("测试建造者模式")
    public void test1() {
        BaseInfo baseInfo = BaseInfo.builder().baseInfoId(1L).name("小明").build();
        log.info(baseInfo.toString());
    }
}
