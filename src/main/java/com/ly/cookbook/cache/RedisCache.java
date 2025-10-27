package com.ly.cookbook.cache;

import com.ly.cookbook.common.units.SpringContextUtil;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Description: RedisTemplate 工具类
 *
 * @author: liu yi
 * @date: 2024-08-01 10:32
 */
public class RedisCache {

    public static RedisTemplate<String, Object> cache = (RedisTemplate<String, Object>) SpringContextUtil.getBean(RedisTemplate.class);

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public static void setObject(final String key, final Object value) {
        cache.opsForValue().set(key, value);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public static <T> void setObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {
        cache.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setList(final String key, final List<T> dataList) {
        Long count = cache.opsForList().rightPushAll(key, dataList);
        return count == null ? 0 : count;
    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setSet(final String key, final Set<T> dataSet) {
        BoundSetOperations<String, T> setOperation = (BoundSetOperations<String, T>) cache.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext()) {
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setMap(final String key, final Map<String, T> dataMap) {
        if (dataMap != null) {
            cache.opsForHash().putAll(key, dataMap);
        }
    }

    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setMapValue(final String key, final String hKey, final T value) {
        cache.opsForHash().put(key, hKey, value);
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public static <T> T getObject(final String key) {
        ValueOperations<String, T> operation = (ValueOperations<String, T>) cache.opsForValue();
        return operation.get(key);
    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getList(final String key) {
        return (List<T>) cache.opsForList().range(key, 0, -1);
    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getSet(final String key) {
        return (Set<T>) cache.opsForSet().members(key);
    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<T, T> getMap(final String key) {
        return (Map<T, T>) cache.opsForHash().entries(key);
    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getMapValue(final String key, final String hKey) {
        HashOperations<String, String, T> opsForHash = cache.opsForHash();
        return opsForHash.get(key, hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiMapValue(final String key, final Collection<Object> hKeys) {
        return (List<T>) cache.opsForHash().multiGet(key, hKeys);
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public static Boolean removeObject(final String key) {
        return cache.delete(key);
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public static Long removeObject(final Collection collection) {
        return cache.delete(collection);
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hKey
     */
    public void removeMapValue(final String key, final String hKey) {
        HashOperations hashOperations = cache.opsForHash();
        hashOperations.delete(key, hKey);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        return expire(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        return cache.expire(key, timeout, unit);
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        return cache.keys(pattern);
    }
}
