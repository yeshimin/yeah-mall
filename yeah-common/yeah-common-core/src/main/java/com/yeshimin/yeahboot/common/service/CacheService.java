package com.yeshimin.yeahboot.common.service;

import com.alibaba.fastjson2.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final StringRedisTemplate redisTemplate;

    /**
     * set with timeout seconds
     */
    public void set(String key, String value, long timeout) {
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    /**
     * get string value
     */
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * get by class
     */
    public <T> T get(String key, Class<T> clazz) {
        return JSON.parseObject(redisTemplate.opsForValue().get(key), clazz);
    }

    /**
     * get hash
     */
    public Map<Object, Object> getHash(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * set hash
     */
    public void setHash(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * set hash
     */
    public void setHash(String key, String field, String value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * delete keys
     */
    public Boolean delete(String... keys) {
        if (keys == null || keys.length == 0) {
            return false;
        }
        Long deletedCount = redisTemplate.delete(Arrays.asList(keys));
        return deletedCount != null && deletedCount > 0;
    }

    /**
     * delete key fields
     */
    public Boolean deleteHashFields(String key, Object... fields) {
        if (fields == null || fields.length == 0) {
            return false;
        }
        return redisTemplate.opsForHash().delete(key, fields) > 0;
    }

    /**
     * expire
     */
    public Boolean expire(String key, long timeoutSeconds) {
        return redisTemplate.expire(key, timeoutSeconds, TimeUnit.SECONDS);
    }

    /**
     * expireAt
     */
    public Boolean expireAt(String key, int expireAtS) {
        return redisTemplate.expireAt(key, new Date(expireAtS * 1000L));
    }

    /**
     * getExpire seconds
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * exists
     */
    public Boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
}
