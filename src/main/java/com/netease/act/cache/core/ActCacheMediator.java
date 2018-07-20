package com.netease.act.cache.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.netease.act.cache.bean.CacheContext;
import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.service.EvictQueueService;
import com.netease.act.cache.util.ExpUtil;
import com.netease.act.cache.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ActCacheMediator implements InitializingBean {

    public static final int MAXIMUM_SIZE = 5210;

    public static final int DURATION = 10;

    @Autowired
    RedisTemplate<String, Object> mRedis;

    ActivityCacheManager mCacheManager;


    @Autowired
    EvictQueueService mQueue;


    @Override
    public void afterPropertiesSet() throws Exception {
    }


    /**
     * 消除key，通过队列统一调度
     *
     * @param cacheNme
     * @param key
     */
    public void evictToQueue(String cacheNme, Object key) {
        ExpUtil.check(key != null);
        String keyStr = String.valueOf(key);
        EvictBO evictBO = EvictBO.builder()
                                 .key(keyStr)
                                 .cacheName(cacheNme)
                                 .build();

        mQueue.put(evictBO);
    }


    /**
    * get  value from local cache and redis cache
     * @param cacheName
     * @param key
     * @return
     */
    public Object get(String cacheName, String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        try {
            return mCacheManager.getCacheByName(cacheName).getLoadingCache().getUnchecked(key);
        } catch (Exception e) {
            LoggerUtil.error(log, "ActCacheMediator get null value exception", e);
        }
        return null;
    }


    /**
    *
    *  put value to double cache
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, Object key, Object value) {
        if (StringUtils.isEmpty(cacheName) || key == null) {
            return;
        }
        try {
            //add to redis
            mRedis.opsForValue().set(getKey(cacheName, key), value);
            //add to local cache
            mCacheManager.getCacheByName(cacheName).getLoadingCache().put(key, value);
            LoggerUtil.debug(log, "ActCacheMediator local cache get key:{},value:{}", key);
        } catch (Exception e) {
            LoggerUtil.error(log, "ActCacheMediator get null value exception", e);
        }
    }

    public void clearLocalAndRemoteCache(String cache) {


    }


    /**
     * @return
     */
    public CacheContext getCacheBuilder() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                                                                .maximumSize(MAXIMUM_SIZE)
                                                                .refreshAfterWrite(DURATION, TimeUnit.SECONDS);
        CacheContext cacheContext = CacheContext.builder()
                                                .cacheBuilder(cacheBuilder)
                                                .build();

        return cacheContext;
    }

    public CacheLoader<Object, Object> getCacheLoad(String cacheName) {
        return new RefreshAsyncCacheLoader<Object, Object>() {

                @Override
                public Object load(Object key) throws Exception {
                    Object o = mRedis.opsForValue().get(getKey(cacheName,key));
                    return o;
                }
            };
    }

    public void setCacheManager(ActivityCacheManager mCacheManager) {
        this.mCacheManager = mCacheManager;
    }

    private String getKey(String cacheName, Object key) {
        return "act:cacheName:" + cacheName + "_" + key;
    }
}
