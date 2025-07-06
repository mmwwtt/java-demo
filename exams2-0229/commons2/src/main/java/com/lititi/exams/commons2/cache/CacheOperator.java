package com.lititi.exams.commons2.cache;

import com.lititi.exams.commons2.enumeration.OP;
import com.lititi.exams.commons2.enumeration.RedisDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class CacheOperator {

    // 注：这里的值是为了查询redis里的session，不能修改；除非redis里session的key空间要换
    public static final String REDIS_SESSION_KEY_NAMESPACE = "spring:session:";

    @Autowired
    private LttCacheManager cacheManager;

    public String getRedisSessionKeyNamespace() {
        return REDIS_SESSION_KEY_NAMESPACE + "sessions:";
    }

    public Object get(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).get(key);
    }

    public void set(String key, Object value, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).set(key, value);
    }

    public void subscribe(String key, MessageListener listener, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).subscribe(key, listener);
    }

    public void pSubscribe(String key, MessageListener listener, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).pSubscribe(key, listener);
    }

    public void publish(String key, Object value, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).publish(key, value);
    }

    public void batchPublish(Map<String, Object> map, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).batchPublish(map);
    }

    public void batchSet(Map<String, Object> map, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).batchSet(map);
    }

    public List<Object> batchGet(List<String> list, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).batchGet(list);
    }

    public void setEx(String key, Object value, long timeout, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).setEx(key, value, timeout);
    }

    /**
     * 仅在key不存在的时候设置value
     *
     * @param key
     * @param value
     * @return
     */
    public Boolean setIfAbsent(String key, Object value, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.WRITE).setIfAbsent(key, value);
    }

    public void expireKey(String key, long seconds, RedisDB db) {
        cacheManager.getCacheByDBAndOP(db, OP.WRITE).expireKey(key, seconds);
    }

    public Long ttl(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).ttl(key);
    }

    public Long delete(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.WRITE).delete(key);
    }

    public Long increment(String key, long delta, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.WRITE).increment(key, delta);
    }

    public Boolean zadd(String key, double score, byte[] value, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.WRITE).zadd(key, score, value);
    }

    public Double zScore(String key, byte[] value, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).zScore(key, value);
    }

    public Boolean hSet(String key, String field, Object value, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.WRITE).hSet(key, field, value);
    }

    public Object hGet(String key, String field, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).hGet(key, field);
    }

    public byte[] hGet(byte[] key, byte[] field, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).hGet(key, field);
    }

    public Map<String, Object> hGetAll(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).hGetAll(key);
    }

    public Map<String, Object> hGetAllWithJdkDeserialize(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).hGetAllWithJdkDeserialize(key);
    }

    public Boolean hSetWithJdkDeserialize(String key, String field, Object value, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).hSetWithJdkDeserialize(key, field, value);
    }

    public Set<Object> keys(String key, RedisDB db) {
        return cacheManager.getCacheByDBAndOP(db, OP.READ).keys(key);
    }

}
