package com.mmwwtt.demo.ee.guava;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class Demo {
    /**
     * 布隆过滤器(存在误报可能)
     * put : 放对象
     * mightContain： 判断布隆过滤器中是否包含该元素
     *
     */
    @Test
    public void bloomFilter() {
        int total = 1000000;
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), total, 0.01);
        for (int i = 0; i < total; i++) {
            bloomFilter.put("" + i);
        }

        boolean isexist = bloomFilter.mightContain("2");
        log.info("{}", isexist);
    }
}

