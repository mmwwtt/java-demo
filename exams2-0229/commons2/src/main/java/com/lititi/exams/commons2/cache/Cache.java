package com.lititi.exams.commons2.cache;

import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Cache {
    String getName();

    /**
     * Return the the underlying native cache provider.
     */
    RedisTemplate<String, String> getNativeCache();

    Object get(final String key);

    void set(final String key, final Object value);

    void batchSet(final Map<String, Object> map);

    List<Object> batchGet(final List<String> list);

    void setEx(final String key, final Object value, long timeout);

    /**
     * 仅在key不存在的时候设置value
     *
     * @param key
     * @param value
     * @return
     */
    Boolean setIfAbsent(String key, Object value);

    void expireKey(final String key, long seconds);

    Long ttl(final String key);

    Long delete(final String key);

    Long increment(String key, final long delta);

    Set<Object> keys(final String key);

    /**
     * 获取有序集合的排序分值
     */
    Double zScore(String key, byte[] value);

    /**
     * 添加到有序集合
     */
    Boolean zadd(String key, double score, byte[] value);

    void subscribe(final String key, final MessageListener listener);

    void pSubscribe(final String patterns, final MessageListener listener);

    Object publish(final String key, final Object value);

    void batchPublish(final Map<String, Object> map);

    // ====================hashset 数据结构 保存==============
    Boolean hSet(String key, String field, Object value);

    // ====================hashset 数据结构 获取==============
    Object hGet(String key, String field);

    Map<String, Object> hGetAll(String key);

    Map<String, Object> hGetAllWithJdkDeserialize(String key);

    byte[] hGet( byte[] key,  byte[] field);

    Boolean hSetWithJdkDeserialize(String key, String field, Object value);
}
