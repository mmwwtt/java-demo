package com.mmwwtt.stock.test;

import com.mmwwtt.stock.service.impl.CommonService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class BuildTest {

    @Resource
    private CommonService commonService;

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        commonService.buildStrateResultLevel1();
    }
}
