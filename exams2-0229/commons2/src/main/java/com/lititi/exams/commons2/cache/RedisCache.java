package com.lititi.exams.commons2.cache;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

public class RedisCache implements Cache {

    private final String name;
    private final RedisTemplate<String, String> template;
    private final long expiration;

    /**
     * Constructs a new <code>RedisCache</code> instance.
     *
     * @param name
     *            cache name
     * @param template
     * @param expiration
     */
    public RedisCache(String name, RedisTemplate<String, String> template, long expiration) {
        Assert.hasText(name, "non-empty cache name is required");
        this.name = name;
        this.template = template;
        this.expiration = expiration;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc} This implementation simply returns the RedisTemplate used for configuring the cache, giving access
     * to the underlying Redis store.
     */
    @Override
    public RedisTemplate<String, String> getNativeCache() {
        return template;
    }

    private byte[] serializeKey(final String key) {
        return template.getStringSerializer().serialize(key);
    }

    @SuppressWarnings("unchecked")
    private byte[] serializeValue(Object value) {
        RedisSerializer<Object> reidsSerializer = (RedisSerializer<Object>)template.getValueSerializer();
        return reidsSerializer.serialize(value);
    }

    @Override
    public Object get(final String key) {
        return template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = serializeKey(key);
                if (connection.exists(keyBytes)) {
                    byte[] valueBytes = connection.get(keyBytes);
                    return template.getValueSerializer().deserialize(valueBytes);
                }
                return null;
            }
        });
    }

    @Override
    public void set(final String key, final Object value) {
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.set(serializeKey(key), serializeValue(value));
                if (expiration > 0) {
                    connection.expire(serializeKey(key), expiration);
                }
                return null;
            }
        });
    }

    /**
     * @Title: subscribe @Description: TODO() @param @param key @param @param value @return void @throws
     */
    @Override
    public void subscribe(final String key, final MessageListener listener) {
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.subscribe(listener, serializeKey(key));
                return null;
            }
        });
    }

    @Override
    public void pSubscribe(final String patterns, final MessageListener listener) {
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.pSubscribe(listener, serializeKey(patterns));
                return null;
            }
        });
    }

    @Override
    public Object publish(final String key, final Object value) {
        return template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.publish(serializeKey(key), serializeValue(value));
            }
        });
    }

    @Override
    public void batchPublish(final Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : map.keySet()) {
                    connection.publish(serializeKey(key), serializeValue(map.get(key)));
                }
                return null;
            }
        }, false, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Object> keys(final String key) {
        return (Set<Object>)template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = serializeKey(key);
                Set<byte[]> set = connection.keys(keyBytes);
                if (CollectionUtils.isEmpty(set)) {
                    return null;
                }
                Set<Object> objSet = new HashSet<Object>();
                for (byte[] bytes : set) {
                    objSet.add(template.getStringSerializer().deserialize(bytes));
                }
                return objSet;
            }
        });
    }

    @Override
    public void batchSet(final Map<String, Object> map) {
        if (CollectionUtils.isEmpty(map)) {
            return;
        }
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                for (String key : map.keySet()) {
                    connection.set(serializeKey(key), serializeValue(map.get(key)));
                }
                return null;
            }
        }, false, true);
    }

    @Override
    public List<Object> batchGet(final List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return template.executePipelined(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                for (int i = 0; i < list.size(); i++) {
                    byte[] keyBytes = serializeKey(list.get(i));
                    connection.get(keyBytes);
                }
                return null;
            }
        }, template.getValueSerializer());
    }

    @Override
    public void setEx(final String key, final Object value, final long timeout) {
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.setEx(serializeKey(key), timeout, serializeValue(value));
                return null;
            }
        });
    }

    @Override
    public Boolean setIfAbsent(final String key, final Object value) {
        return (Boolean)template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.setNX(serializeKey(key), serializeValue(value));
            }
        });
    }

    @Override
    public void expireKey(final String key, final long seconds) {
        template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                byte[] keyBytes = serializeKey(key);
                connection.expire(keyBytes, seconds);
                return null;
            }
        });
    }

    @Override
    public Long ttl(final String key) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.ttl(serializeKey(key));
            }
        });
    }

    @Override
    public Long delete(final String key) {
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.del(serializeKey(key));
            }
        });
    }

    @Override
    public Long increment(String key, final long delta) {
        final byte[] rawKey = serializeKey(key);
        return template.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection connection) {
                return connection.incrBy(rawKey, delta);
            }
        }, true);
    }

    @Override
    public Boolean zadd(String key, final double score, final byte[] value) {
        final byte[] rawKey = serializeKey(key);
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.zAdd(rawKey, score, value);
            }
        }, true);
    }

    @Override
    public Double zScore(String key, final byte[] value) {
        final byte[] rawKey = serializeKey(key);
        return template.execute(new RedisCallback<Double>() {
            @Override
            public Double doInRedis(RedisConnection connection) {
                return connection.zScore(rawKey, value);
            }
        }, true);
    }

    @Override
    public Boolean hSet(final String key, final String field, final Object value) {
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.hSet(serializeKey(key), serializeKey(field), serializeValue(value));
            }
        }, true);
    }

    @Override
    public Object hGet(final String key, final String field) {
        return template.execute(new RedisCallback<Object>() {
            @Override
            public Object doInRedis(RedisConnection connection) {
                byte[] valueBytes = connection.hGet(serializeKey(key), serializeKey(field));
                return template.getValueSerializer().deserialize(valueBytes);
            }
        }, true);
    }

    @Override
    public byte[] hGet(final byte[] key, final byte[] field) {
        return template.execute(new RedisCallback<byte[]>() {
            @Override
            public byte[] doInRedis(RedisConnection connection) {
                byte[] valueBytes = connection.hGet(key, field);
                return valueBytes;
            }
        }, true);
    }

    @Override
    public Boolean hSetWithJdkDeserialize(String key, String field, Object value) {
        JdkSerializationRedisSerializer jdk = new JdkSerializationRedisSerializer();
        return template.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection) {
                return connection.hSet(serializeKey(key), serializeKey(field), jdk.serialize(value));
            }
        }, true);
    }

    @Override
    public Map<String, Object> hGetAll(final String key) {
        return template.execute(new RedisCallback<Map<String, Object>>() {
            @Override
            public Map<String, Object> doInRedis(RedisConnection connection) {
                Map<byte[], byte[]> map = connection.hGetAll(serializeKey(key));
                if (map == null || map.isEmpty()) {
                    return null;
                }
                Map<String, Object> resultMap = new HashMap<String, Object>();
                for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                    String key = template.getStringSerializer().deserialize(entry.getKey());
                    Object value = template.getHashValueSerializer().deserialize(entry.getValue());
                    resultMap.put(key, value);
                }

                return resultMap;
            }
        }, true);
    }

    @Override
    public Map<String, Object> hGetAllWithJdkDeserialize(String key) {
        JdkSerializationRedisSerializer jdk = new JdkSerializationRedisSerializer();
        return template.execute(new RedisCallback<Map<String, Object>>() {
            @Override
            public Map<String, Object> doInRedis(RedisConnection connection) {
                Map<byte[], byte[]> map = connection.hGetAll(serializeKey(key));
                if (map == null || map.isEmpty()) {
                    return null;
                }
                Map<String, Object> resultMap = new HashMap<String, Object>();
                for (Map.Entry<byte[], byte[]> entry : map.entrySet()) {
                    String key = template.getStringSerializer().deserialize(entry.getKey());
                    Object value = jdk.deserialize(entry.getValue());
                    resultMap.put(key, value);
                }

                return resultMap;
            }
        }, true);
    }

}
