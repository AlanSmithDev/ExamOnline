package com.sdut.examonline.web.util;

import com.github.benmanes.caffeine.cache.Cache;
import com.sdut.examonline.common.constant.CacheConstants;
import com.sdut.examonline.common.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author Chen
 * @since 2022/3/31 16:37
 */
@Slf4j
@Component
public class CacheUtils implements MessageListener {

    public static final String DELETE_KEY_TOPIC = "delkey";

    public static final String KEY_MESSAGAE_QUEUE_PREFIX = "mq:";

    private final String UNIQUE_ID = RandomStringUtils.randomAlphabetic(8);

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    Cache<String, Object> localCache;

    public RedisTemplate<String, String> getRedisTemplate() {
        return redisTemplate;
    }

    public void setLocal(String key, Object value) {
        if (value == null) {
            deleteLocal(key);
            return;
        }
        localCache.put(key, value);
    }

    public Object getLocal(String key) {
        return localCache.getIfPresent(key);
    }

    public void deleteLocal(String key) {
        localCache.invalidate(key);
    }

    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : JsonUtils.toJson(value));
            broadcastKeyUpdate(key);
            setLocal(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : JsonUtils.toJson(value), time, timeUnit);
            broadcastKeyUpdate(key);
            setLocal(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setString(String key, String value) {
        try {
            redisTemplate.opsForValue().set(key, value == null ? CacheConstants.NULL_STRING_VALUE : value);
            broadcastKeyUpdate(key);
            setLocal(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setString(String key, String value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value == null ? CacheConstants.NULL_STRING_VALUE : value, time, timeUnit);
            broadcastKeyUpdate(key);
            setLocal(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public boolean setIfPresent(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfPresent(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : JsonUtils.toJson(value), time, timeUnit);
            if (result != null && result) {
                broadcastKeyUpdate(key);
                setLocal(key, value);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean setStringIfPresent(String key, String value, long time, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfPresent(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : value, time, timeUnit);
            if (result != null && result) {
                broadcastKeyUpdate(key);
                setLocal(key, value);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean setIfAbsent(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : JsonUtils.toJson(value), time, timeUnit);
            if (result != null && result) {
                broadcastKeyUpdate(key);
                setLocal(key, value);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public boolean setStringIfAbsent(String key, String value, long time, TimeUnit timeUnit) {
        try {
            Boolean result = redisTemplate.opsForValue().setIfAbsent(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : value, time, timeUnit);
            if (result != null && result) {
                broadcastKeyUpdate(key);
                setLocal(key, value);
                return true;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    public void setExpireAt(String key, Object value, Date time) {
        try {
            redisTemplate.opsForValue().set(key, value == null ? CacheConstants.EMPTY_OBJECT_VALUE : JsonUtils.toJson(value));
            redisTemplate.expireAt(key, time);
            broadcastKeyUpdate(key);
            setLocal(key, value);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public <T> T getOrProvide(String key, Class<T> clazz, Supplier<T> provider) {
        Object value = get(key, clazz, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (isEmptyObject(value)) {
            return null;
        }
        if (value != null) {
            return (T) value;
        }
        T t = provider.get();
        if (t != null) {
            set(key, t, 10, TimeUnit.MINUTES);
        }
        return t;
    }

    public Object getOrProvide(String key, Supplier<String> provider) {
        String value = getString(key, 10, TimeUnit.MINUTES);
        //设置为空对象，避免缓存击穿
        if (isNullString(value)) {
            return null;
        }
        if (value != null) {
            return value;
        }
        value = provider.get();
        set(key, value == null ? CacheConstants.NULL_STRING_VALUE : value, 10, TimeUnit.MINUTES);
        return value;
    }

    public <T> T getNullable(String key, Class<T> clazz) {
        return getNullable(key, clazz, 0L, null);
    }

    public <T> T getNullable(String key, Class<T> clazz, long expireTime, TimeUnit timeUnit) {
        Object value = getLocal(key);
        if (value != null) {
            return (T) value;
        }
        String valueStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(valueStr) || CacheConstants.EMPTY_OBJECT_VALUE.equals(valueStr)) {
            return null;
        }
        try {
            T t = JsonUtils.from(valueStr, clazz);
            setLocal(key, t);
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, timeUnit);
            }
            return t;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public String getStringNullable(String key) {
        return getStringNullable(key, 0L, null);
    }

    public String getStringNullable(String key, long expireTime, TimeUnit timeUnit) {
        Object value = getLocal(key);
        if (value != null) {
            return (String) value;
        }
        String valueStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(valueStr) || CacheConstants.NULL_STRING_VALUE.equals(valueStr)) {
            return null;
        }
        try {
            setLocal(key, valueStr);
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, timeUnit);
            }
            return valueStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public Object get(String key, Class<?> clazz) {
        return get(key, clazz, 0L, null);
    }

    public Object get(String key, Class<?> clazz, long expireTime, TimeUnit timeUnit) {
        Object value = getLocal(key);
        if (value != null) {
            return value;
        }
        String valueStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(valueStr)) {
            setLocal(key, CacheConstants.EMPTY_OBJECT);
            return null;
        }
        if (CacheConstants.EMPTY_OBJECT_VALUE.equals(valueStr)) {
            setLocal(key, CacheConstants.EMPTY_OBJECT);
            return CacheConstants.EMPTY_OBJECT;
        }
        try {
            value = JsonUtils.from(valueStr, clazz);
            setLocal(key, value);
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, timeUnit);
            }
            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return CacheConstants.EMPTY_OBJECT;
        }
    }

    public String getString(String key) {
        return getString(key, 0L, null);
    }

    public String getString(String key, long expireTime, TimeUnit timeUnit) {
        Object value = getLocal(key);
        if (value != null) {
            if (CacheConstants.NULL_STRING_VALUE.equals(value)) {
                return null;
            }
            return (String) value;
        }
        String valueStr = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(valueStr)) {
            setLocal(key, CacheConstants.NULL_STRING_VALUE);
            return null;
        }
        if (CacheConstants.NULL_STRING_VALUE.equals(valueStr)) {
            setLocal(key, CacheConstants.NULL_STRING_VALUE);
            return null;
        }
        try {
            setLocal(key, valueStr);
            if (expireTime > 0) {
                redisTemplate.expire(key, expireTime, timeUnit);
            }
            return valueStr;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public void delete(String key) {
        deleteLocal(key);
        redisTemplate.delete(key);
        broadcastKeyUpdate(key);
    }

    public boolean isEmptyObject(Object value) {
        return CacheConstants.EMPTY_OBJECT.equals(value);
    }

    public boolean isNullString(String value) {
        return CacheConstants.NULL_STRING_VALUE.equals(value);
    }

    private void broadcastKeyUpdate(String key) {
        redisTemplate.convertAndSend(DELETE_KEY_TOPIC, UNIQUE_ID + "," + key);
    }

    public Long leftPush(String key, String value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    public String rightPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String topic = new String(bytes);
        if (DELETE_KEY_TOPIC.equals(topic)) {
            String body = new String(message.getBody());
            String[] split = body.split(",");
            if (!UNIQUE_ID.equals(split[0])) {
                deleteLocal(split[1]);
            }
        }
    }

}
