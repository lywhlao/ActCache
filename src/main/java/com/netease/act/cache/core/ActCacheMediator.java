package com.netease.act.cache.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.netease.act.cache.bean.CacheContext;
import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.service.EvictQueueService;
import com.netease.act.cache.util.ExpUtil;
import com.netease.act.cache.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ActCacheMediator implements InitializingBean {

    public static final int MAXIMUM_SIZE = 5210;

    public static final int DURATION = 10;

    RedisTemplate<String, Object> mRedis;

    LoadingCache<String, Object> mLocalCache;

    @Autowired
    EvictQueueService mQueue;


    @Override
    public void afterPropertiesSet() throws Exception {
        initLocalCache();
    }


    /**
    *  消除key，通过队列统一调度
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


    public Object get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object result = null;
        try {
            result = mLocalCache.get(key);
            LoggerUtil.debug(log, "ActCacheMediator local cache get key:{},result:{}", key, result);
        } catch (Exception e) {
            LoggerUtil.error(log, "ActCacheMediator get null value exception", e);
        }
        return result;
    }

    public void put(String cacheName,String key,String value) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Object result = null;
        try {
            result = mLocalCache.get(key);
            LoggerUtil.debug(log, "ActCacheMediator local cache get key:{},result:{}", key, result);
        } catch (Exception e) {
            LoggerUtil.error(log, "ActCacheMediator get null value exception", e);
        }
        return result;
    }





    public void clearLocalAndRemoteCache(String cache){


    }


    public CacheContext getCacheBuilder(){
        CacheBuilder cacheBuilder = CacheBuilder.newBuilder().refreshAfterWrite(10,TimeUnit.SECONDS);

    }


    private void initLocalCache() {
        mLocalCache = CacheBuilder
                .newBuilder()
                .maximumSize(MAXIMUM_SIZE)
                .refreshAfterWrite(DURATION, TimeUnit.SECONDS)
                .build(new RefreshAsyncCacheLoader<String, Object>() {
                    @Override
                    public Object load(String key) throws Exception {
                        Object o = mRedis.opsForValue().get(key);
                        return o;
                    }
                });
    }

}
