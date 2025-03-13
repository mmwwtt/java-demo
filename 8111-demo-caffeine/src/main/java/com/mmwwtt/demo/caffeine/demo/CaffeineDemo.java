package com.mmwwtt.demo.caffeine.demo;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CaffeineDemo {

    /**
     * 创建自动加载的缓存
     */
    @Test
    public void demo1() {
        // 定义缓存加载器
        CacheLoader<String, String> loader = new CacheLoader<String, String>() {
            @Override
            public String load(String key) throws Exception {
                // 模拟从数据源加载数据
                return "Loaded value for " + key;
            }
        };

        // 创建自动加载的缓存(build中的loader自动查询数据并加载, 空则不自动加载)
        LoadingCache<String, String> cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(loader);

        // 获取缓存项，如果缓存中不存在，则会自动调用加载器加载数据
        System.out.println(cache.get("key2"));
    }

    /**
     * 创建异步缓存
     */
    @Test
    public void demo2() {
        AsyncCache<String, String> asyncCache = Caffeine.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .buildAsync();

        // 异步获取缓存值
        CompletableFuture<String> future = asyncCache.get("key1", key -> "default-value");
        future.thenAccept(System.out::println);
    }

}
