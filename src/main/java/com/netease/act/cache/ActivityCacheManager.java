package com.netease.act.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.netease.act.cache.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by hzlaojiaqi on 2017/11/28.
 */
public class ActivityCacheManager extends AbstractCacheManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivityCacheManager.class);

    RedisTemplate<String, Object> mRedis;


    LoadingCache<String, Object> cache = CacheBuilder
            .newBuilder()
            .maximumSize(5210)
            .refreshAfterWrite(10, TimeUnit.SECONDS)
            .build(new RefreshAsyncCacheLoader<String, Object>() {
                @Override
                public Object load(String key) throws Exception {
                    Object o = mRedis.opsForValue().get(key);
                    Thread.sleep(2000);
                    LoggerUtil.debug(LOGGER, "activityManager local cache reload key:{},result:{}", key, o);
                    return o;
                }
            });


    @Override
    protected Collection<? extends Cache> loadCaches() {
        Set<ActCache> actCacheSet = new HashSet<ActCache>();
        actCacheSet.add(new ActCache("actCache", this));
        return actCacheSet;
    }

    public Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object result = null;
        try {
            result = cache.get(key);
            LoggerUtil.debug(LOGGER, "activityManager local cache get key:{},result:{}", key, result);
        } catch (Exception e) {
            LoggerUtil.error(LOGGER, "ActivityCacheManager get null value exception", e);
        }
        return result;
    }


    public void put(Object key, Object value) {
        if (StringUtils.isEmpty(key)) {
            return;
        }
        mRedis.opsForValue().set(String.valueOf(key), value);
        LoggerUtil.debug(LOGGER, "activityManager put redist cache key:{},value:{}", key, value);
    }

    public void evict(Object key) {
        mRedis.delete(String.valueOf(key));
    }

    public Object putIfAbsent(Object key, Object value) {
        Boolean setStatus = mRedis.opsForValue().setIfAbsent(String.valueOf(key), value);
        if (setStatus) {
            return value;
        }
        return null;
    }


}
