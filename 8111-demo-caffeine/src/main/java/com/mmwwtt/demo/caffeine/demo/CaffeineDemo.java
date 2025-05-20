package com.mmwwtt.demo.caffeine.demo;

import com.github.benmanes.caffeine.cache.*;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class CaffeineDemo {

    /**
     * 创建自动加载的缓存
     */
    @Test
    public void demo1() {
        // 定义缓存加载器
        CacheLoader<String, String> loader = key -> {
            // 模拟从数据源加载数据
            return "Loaded value for " + key;
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

    @Test
    public void demo3() {
        String key = "小明";
        String value = "hello";

        //创建缓存
        Cache<String, String> cache = Caffeine.newBuilder()
                .initialCapacity(100)
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)   //写入后多久过期
                .expireAfterAccess(10, TimeUnit.MINUTES)  //访问后多久后期
                .removalListener((k, v, cause) -> {               //监听移除动作后回调
                    System.out.println("Key: " + k + " was removed due to " + cause);
                })
                .recordStats() // 启用统计
                .build();


        //创建异步加载缓存
        AsyncCache<String, String> cache1 = Caffeine.newBuilder()
                .buildAsync();

        //放入缓存
        cache.put(key, value);

        //获取缓存，不存在则返回null
        String valueTmp = cache.getIfPresent(key);

        //获取所有缓存内容
        Map<String, String> allCache = cache.asMap();

        //清除缓存
        cache.invalidate(key);
        cache.invalidateAll();

        // 获取缓存统计信息
        CacheStats stats = cache.stats();
        System.out.println("缓存命中次数：" + stats.hitCount());
        System.out.println("缓存未命中次数：" + stats.missCount());
        System.out.println("缓存命中率：" + stats.hitRate());
    }

}
