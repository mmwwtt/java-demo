package com.mmwwtt.demo.ee.guava;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheStats;
import com.google.common.cache.RemovalListener;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCacheDemo {

    public static void main(String[] args) throws ExecutionException {
        String key = "小明";
        String value = "hello";

        //创建缓存
        Cache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(10, TimeUnit.MINUTES)   //写入后多久过期
                .expireAfterAccess(10, TimeUnit.MINUTES)  //访问后多久后期
                .removalListener((RemovalListener<String, String>) notification -> {   //监听移除动作后回调
                    System.out.println("Key: " + notification.getKey() + " was removed");
                })
                .recordStats()  //启用统计
                .build();
        //放入缓存
        cache.put(key, value);

        //从缓存中获取值，不存在则返回null
        String valueTmp = cache.getIfPresent(key);

        //从缓存中获取值，不存在则执行自定义的返回
        valueTmp = cache.get(key, () -> "执行回调方法，返回自定义的值");

        //删除key
        cache.invalidate(key);

        //清空所有缓存
        cache.invalidateAll();

        CacheStats stats = cache.stats();
        System.out.println("缓存命中率：" + stats.hitRate());
        System.out.println("缓存加载次数：" + stats.loadCount());
        System.out.println("缓存加载失败次数：" + stats.loadExceptionCount());
    }
}
