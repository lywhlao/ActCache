package com.netease.act.cache.core;

import com.google.common.cache.LoadingCache;
import com.netease.act.cache.bean.NullObject;
import com.netease.act.cache.bean.excpetion.ActNotSupportException;
import com.netease.act.cache.constant.Constant;
import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * Created by hzlaojiaqi on 2017/11/28.
 */
public class ActCache implements Cache {

    String name;

    ActCacheMediator mMediator;

    LoadingCache<Object, Object> mLoadingCache;

    public ActCache(String name, ActCacheMediator mediator) {
        this.name = name;
        this.mMediator = mediator;
    }

    public ActCache(String name, ActCacheMediator mMediator, LoadingCache<Object, Object> mLoadingCache) {
        this.name = name;
        this.mMediator = mMediator;
        this.mLoadingCache = mLoadingCache;
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
        Object o = mMediator.get(this.name,String.valueOf(key));
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
        T t = mMediator.get(this.name, key, valueLoader);
        return t;
    }

    @Override
    public void put(Object key, Object value) {
        mMediator.put(name,key,value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        // local cache and redis can not be in same transaction
        throw new ActNotSupportException();
    }

    @Override
    public void evict(Object key) {
        mMediator.evictToQueue(this.name, key, Constant.PHASE_BROADCAST);
    }

    @Override
    public void clear() {
      //Todo
    }

    public LoadingCache<Object, Object> getLoadingCache() {
        return mLoadingCache;
    }


}
