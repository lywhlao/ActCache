package com.netease.act.cache.core;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * Created by hzlaojiaqi on 2017/11/28.
 */
public class ActCache implements Cache {

    String name;

    ActivityCacheManager mCacheManager;

    public ActCache(String name, ActivityCacheManager cacheManager) {
        this.name = name;
        this.mCacheManager = cacheManager;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.mCacheManager;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object o = mCacheManager.get(String.valueOf(key));
        return o==null?null:new SimpleValueWrapper(o);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object o = get(key).get();
        if(o==null){
            return null;
        }
        return (T)o;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        //TOdo
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        mCacheManager.put(key,value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return new SimpleValueWrapper(mCacheManager.putIfAbsent(key,value));
    }

    @Override
    public void evict(Object key) {
        mCacheManager.evict(key);
    }

    @Override
    public void clear() {

    }
}
