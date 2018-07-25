package com.netease.act.cache.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.netease.act.cache.bean.CacheContext;
import com.netease.act.cache.bean.EvictBO;
import com.netease.act.cache.constant.Constant;
import com.netease.act.cache.service.LeaderService;
import com.netease.act.cache.service.QueueService;
import com.netease.act.cache.util.ExpUtil;
import com.netease.act.cache.util.LoggerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ActCacheMediator implements InitializingBean, Constant {

    @Autowired
    RedisTemplate<String, Object> mRedis;

    @Autowired
    LeaderService mLeader;

    ActivityCacheManager mCacheManager;

    @Autowired
    QueueService mQueue;

    @Override
    public void afterPropertiesSet(){
    }


    /**
     * 消除key，通过队列统一调度
     *
     * @param cacheNme
     * @param key
     */
    public void evictToQueue(String cacheNme, Object key, int phase) {
        ExpUtil.check(key != null);
        String keyStr = String.valueOf(key);
        EvictBO evictBO = EvictBO.builder()
                .key(keyStr)
                .cacheName(cacheNme)
                .phase(phase)
                .uuid(UUID.randomUUID().toString())
                .build();

        mQueue.putToEvict(evictBO);
    }

    /**
     * evict cache
     * <p>
     * 1. local cache
     * 2. redis cache
     *
     * @param cacheName
     * @param key
     */
    public void evictByCacheName(String cacheName, Object key) {
        evictLocalCacheByCacheName(cacheName, key);
        evictRedisByCacheName(cacheName, key);
    }

    /**
     * evict cache
     * <p>
     * local cache
     *
     * @param cacheName
     * @param key
     */
    public void evictLocalCacheByCacheName(String cacheName, Object key) {
        ExpUtil.check(key != null);
        ActCache cacheByName = mCacheManager.getCacheByName(cacheName);
        ExpUtil.check(cacheByName != null);
        cacheByName.getLoadingCache().invalidate(key);
    }

    /**
     * evict cache
     * <p>
     * redis cache
     *
     * @param cacheName
     * @param key
     */
    public void evictRedisByCacheName(String cacheName, Object key) {
        if (mLeader.isLeader()) {
            String redisKey = getKey(cacheName, key);
            mRedis.delete(redisKey);
            log.info("leader evict ==>redis key:{}", key);
        }
    }


    /**
     * get  value from local cache and redis cache
     *
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
            //ignore. may be get null value for key
        }
        return null;
    }


    /**
     * put value to double cache
     *
     * @param cacheName
     * @param key
     * @param valueLoader
     */
    public <T> T get(String cacheName, Object key, Callable<T> valueLoader) {
        if (StringUtils.isEmpty(cacheName) || key == null) {
            return null;
        }
        Object o = get(cacheName, String.valueOf(key));
        if (o != null) {
            return (T) o;
        }
        T callValue = null;
        try {
            callValue = valueLoader.call();
            put(cacheName, key, callValue);
        } catch (Exception e) {
            log.error("put value error", e);
        }
        return callValue;
    }


    /**
     * put value to double cache
     *
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
            log.error("ActCacheMediator put null value exception", e);
        }
    }

    public void clearLocalAndRemoteCache(String cache) {


    }


    /**
     * @return
     */
    public CacheContext getCacheBuilder() {
        CacheBuilder<Object, Object> cacheBuilder = CacheBuilder.newBuilder()
                .maximumSize(LOCAL_CACHE_MAXIMUM_SIZE)
                .expireAfterWrite(LOCAL_CACHE_EXPIRE_DURATION, TimeUnit.MINUTES);//TOdo 自动过期时间
        CacheContext cacheContext = CacheContext.builder()
                .cacheBuilder(cacheBuilder)
                .build();

        return cacheContext;
    }

    /**
     * local cache load redis cache
     *
     * @param cacheName
     * @return
     */
    public CacheLoader<Object, Object> getCacheLoad(String cacheName) {
        return new RefreshAsyncCacheLoader<Object, Object>() {

            @Override
            public Object load(Object key) throws Exception {
                Object o = mRedis.opsForValue().get(getKey(cacheName, key));
                //todo reids null value
//                if(o==null){
//                    return NullObject.getInstance();
//                }
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
