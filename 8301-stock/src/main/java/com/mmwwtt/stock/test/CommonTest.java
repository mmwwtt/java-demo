package com.mmwwtt.stock.test;

import com.mmwwtt.stock.service.impl.CalcCommonService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutionException;

@SpringBootTest
public class CommonTest {

    @Resource
    private CalcCommonService calcCommonService;

    @Test
    @DisplayName("根据策略预测")
    public void predict() throws InterruptedException, ExecutionException {
        calcCommonService.predict("20260213", false, 1.2);
    }

    @Test
    @DisplayName("生成level1策略结果")
    public void getL1Strategy() throws InterruptedException, ExecutionException {
        calcCommonService.buildStrateResultLevel1();
    }
}
