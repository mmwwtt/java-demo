package com.mmwwtt.demo.se.基础;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BigDecimalTest {

    @Test
    @DisplayName("stream中比较bigDecimal 取最大值")
    public void test1() {
        List<BigDecimal> list = new ArrayList<>();
        BigDecimal fiveDayLow = list.stream().min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        BigDecimal sum = list.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
