package com.mmwwtt.demo.caffeine.Service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Cacheable(value = "exampleCache", key = "#key")
    public String getData(String key) {
        // 模拟从数据库加载数据
        System.out.println("Fetching data from database...");
        return "Data for " + key;
    }
}