package com.mmwwtt.demo.ee.guava;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

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
    
    @Test
    public void demoPage() {
        List<Integer> list = new ArrayList<>();
        PageQuery pageQuery = new PageQuery();
        pageQuery.setCurrent(1);
        pageQuery.setSize(10);
        // 内存实现分页
        List<List<Integer>> res = Lists.partition(list, pageQuery.getSize());

        // 如果current超过了总页数，返回空数组
        if (pageQuery.getCurrent() > res.size()) {
            Lists.newArrayList();
        }
        // 返回对应的分区 (注意current在项目中是否从1开始)
        res.get(pageQuery.getCurrent() - 1);
    }
}

