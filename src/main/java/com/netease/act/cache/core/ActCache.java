package com.netease.act.cache.core;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * Created by hzlaojiaqi on 2017/11/28.
 */
public class ActCache implements Cache {

    String name;

    ActCacheMediator mMediator;

    public ActCache(String name, ActCacheMediator mediator) {
        this.name = name;
        this.mMediator = mediator;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object o = mMediator.get(String.valueOf(key));
        return o==null?null:new SimpleValueWrapper(o);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper vp = get(key);
        if(vp==null){
            return null;
        }
        return (T)vp.get();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        //TOdo
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        mMediator.put(key,value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return new SimpleValueWrapper(mCacheManager.putIfAbsent(key,value));
    }

    @Override
    public void evict(Object key) {
        mMediator.evictToQueue(this.name,key);
    }

    @Override
    public void clear() {

    }
}
