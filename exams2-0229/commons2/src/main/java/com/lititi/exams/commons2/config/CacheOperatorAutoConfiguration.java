package com.lititi.exams.commons2.config;

import com.lititi.exams.commons2.cache.CacheOperator;
import com.lititi.exams.commons2.cache.LttCacheManager;
import com.lititi.exams.commons2.cache.RedisCache;
import com.lititi.exams.commons2.cache.serializer.redis.FastJsonSerializer;
import com.lititi.exams.commons2.cache.serializer.redis.LttJDKSerializer;
import com.lititi.exams.commons2.cache.serializer.redis.RedisSessionJavaSerializer;
import com.lititi.exams.commons2.enumeration.RedisDB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

@Configuration
@ConditionalOnMissingBean({CacheOperator.class})
@EnableConfigurationProperties({LttRedisProperties.class})
public class CacheOperatorAutoConfiguration {

    @Autowired
    private LttRedisProperties lttRedisProperties;

    @Bean
    public FastJsonSerializer fastJsonSerializer() {
        return new FastJsonSerializer();
    }

    @Bean
    public LttJDKSerializer lttJDKSerializer() {
        return new LttJDKSerializer();
    }

    @Bean
    public RedisSessionJavaSerializer lttJavaSerializer() {
        return new RedisSessionJavaSerializer();
    }

    /**
     * @return jedisPoolConfig
     */
    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        LttRedisProperties.Pool pool = lttRedisProperties.getPool();
        poolConfig.setMaxTotal(pool.getMaxActive());
        poolConfig.setMaxIdle(pool.getMaxIdle());
        poolConfig.setMinIdle(pool.getMinIdle());
        poolConfig.setMaxWaitMillis(pool.getMaxWait().toMillis());
        Duration timeBetweenEvictionRuns = pool.getTimeBetweenEvictionRuns();
        if (timeBetweenEvictionRuns != null) {
            poolConfig.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRuns.toMillis());
        }
        return poolConfig;
    }

    /**
     * @return jedisConnectionFactoryOtherMaster
     */
    private JedisConnectionFactory getJedisConnectionFactoryOtherMaster() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Other.Master master = lttRedisProperties.getOther().getMaster();
        redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
        redisStandaloneConfiguration.setPassword(master.getPassword());
        redisStandaloneConfiguration.setHostName(master.getHost());
        redisStandaloneConfiguration.setPort(master.getPort());
        redisStandaloneConfiguration.setUsername(master.getUsername());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * @return jedisConnectionFactoryOtherSlave
     */
    private JedisConnectionFactory getJedisConnectionFactoryOtherSlave() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Other.Slave slave = lttRedisProperties.getOther().getSlave();
        redisStandaloneConfiguration.setDatabase(RedisDB.OTHER.value());
        redisStandaloneConfiguration.setPassword(slave.getPassword());
        redisStandaloneConfiguration.setHostName(slave.getHost());
        redisStandaloneConfiguration.setPort(slave.getPort());
        redisStandaloneConfiguration.setUsername(slave.getUsername());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * @return jedisConnectionSessionSlave
     */
    private JedisConnectionFactory getJedisConnectionFactorySessionSlave() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        LttRedisProperties.Session.Slave slave = lttRedisProperties.getSession().getSlave();
        redisStandaloneConfiguration.setDatabase(RedisDB.SESSION.value());
        redisStandaloneConfiguration.setPassword(slave.getPassword());
        redisStandaloneConfiguration.setHostName(slave.getHost());
        redisStandaloneConfiguration.setPort(slave.getPort());
        redisStandaloneConfiguration.setUsername(slave.getUsername());
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    /**
     * @param lttJDKSerializer
     * @return redisTemplateOtherMaster
     */
    @Bean
    public RedisTemplate redisTemplateOtherMaster(LttJDKSerializer lttJDKSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJDKSerializer);
        template.setConnectionFactory(getJedisConnectionFactoryOtherMaster());
        return template;
    }

    /**
     * @param lttJDKSerializer
     * @return redisTemplateOtherSlave
     */
    @Bean
    public RedisTemplate redisTemplateOtherSlave(LttJDKSerializer lttJDKSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJDKSerializer);
        template.setConnectionFactory(getJedisConnectionFactoryOtherSlave());
        return template;
    }

/**
     * @param lttJavaSerializer
     * @param jedisConnectionFactorySessionMaster
     * @return redisTemplateSessionMaster
     */
    @Bean
    public RedisTemplate redisTemplateSessionMaster(RedisSessionJavaSerializer lttJavaSerializer,
        JedisConnectionFactory jedisConnectionFactorySessionMaster) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJavaSerializer);
        template.setConnectionFactory(jedisConnectionFactorySessionMaster);
        return template;
    }

    /**
     * @param lttJavaSerializer
     * @return redisTemplateSessionSlave
     */
    @Bean
    public RedisTemplate redisTemplateSessionSlave(RedisSessionJavaSerializer lttJavaSerializer) {
        RedisTemplate template = new RedisTemplate();
        template.setDefaultSerializer(lttJavaSerializer);
        template.setConnectionFactory(getJedisConnectionFactorySessionSlave());
        return template;
    }

    @Bean
    public LttCacheManager cacheManager(RedisTemplate redisTemplateOtherMaster, RedisTemplate redisTemplateOtherSlave,
        RedisTemplate redisTemplateSessionMaster, RedisTemplate redisTemplateSessionSlave) {
        LttCacheManager cacheManager = new LttCacheManager();
        Set<RedisCache> caches = new HashSet<>();
        RedisCache otherCacheMaster = new RedisCache("OtherCacheMaster", redisTemplateOtherMaster, 0);
        RedisCache otherCacheSlave = new RedisCache("OtherCacheSlave", redisTemplateOtherSlave, 0);
        RedisCache sessionCacheMaster = new RedisCache("SessionCacheMaster", redisTemplateSessionMaster, 0);
        RedisCache sessionVacheSlave = new RedisCache("SessionCacheSlave", redisTemplateSessionSlave, 0);
        caches.add(otherCacheMaster);
        caches.add(otherCacheSlave);
        caches.add(sessionCacheMaster);
        caches.add(sessionVacheSlave);
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Bean
    public CacheOperator cacheOperator() {
        return new CacheOperator();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        return template;
    }
}
